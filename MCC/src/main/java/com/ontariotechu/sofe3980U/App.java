package com.ontariotechu.sofe3980U;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;

public class App {
    public static void main(String[] args) {
        String modelFile = "model.csv";
        File file = new File(modelFile);
        if (!file.exists()) {
            file = new File("src/main/resources/" + modelFile);
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            List<String[]> allData = reader.readAll();
            allData.remove(0);

            double ce = 0;
            int[][] matrix = new int[5][5];
            int n = allData.size();

            for (String[] line : allData) {
                int actual = Integer.parseInt(line[0]);
                double[] predictedProbs = new double[5];
                int bestClass = 0;
                double maxProb = -1.0;

                for (int j = 0; j < 5; j++) {
                    predictedProbs[j] = Double.parseDouble(line[j+1]);
                    if (predictedProbs[j] > maxProb) {
                        maxProb = predictedProbs[j];
                        bestClass = j + 1;
                    }
                }
                
                ce += Math.log(predictedProbs[actual - 1] + 1e-10);
                matrix[bestClass - 1][actual - 1]++;
            }

            System.out.printf("CE =%.7f\n", (-ce / n));
            System.out.println("Confusion matrix");
            System.out.println("\t\ty=1\ty=2\ty=3\ty=4\ty=5");
            for (int i = 0; i < 5; i++) {
                System.out.print("\ty^=" + (i+1) + "\t");
                for (int j = 0; j < 5; j++) {
                    System.out.print(matrix[i][j] + "\t");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Could not find file: " + modelFile);
        }
    }
}