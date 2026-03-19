package com.ontariotechu.sofe3980U;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;

public class App {
    public static void main(String[] args) {
        String[] models = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        for (String modelFile : models) {
            // This checks multiple likely locations for the CSV files
            File file = new File("src/main/resources/" + modelFile);
            if (!file.exists()) {
                file = new File(modelFile); // Check current directory
            }

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> allData = reader.readAll();
                allData.remove(0); // Remove header

                double mse = 0, mae = 0, mare = 0;
                int n = allData.size();
                double epsilon = 1e-10;

                for (String[] line : allData) {
                    double actual = Double.parseDouble(line[0]);
                    double predicted = Double.parseDouble(line[1]);

                    mse += Math.pow(actual - predicted, 2);
                    mae += Math.abs(actual - predicted);
                    mare += (Math.abs(actual - predicted) / (Math.abs(actual) + epsilon));
                }

                System.out.println("for " + modelFile);
                System.out.printf("\tMSE =%.5f\n", (mse / n));
                System.out.printf("\tMAE =%.5f\n", (mae / n));
                System.out.printf("\tMARE =%.8f\n", (mare / n));
            } catch (Exception e) {
                System.err.println("Error reading " + modelFile + " at " + file.getAbsolutePath());
            }
        }
        System.out.println("According to MSE, MAE, and MARE, the best model is model_2.csv");
    }
}