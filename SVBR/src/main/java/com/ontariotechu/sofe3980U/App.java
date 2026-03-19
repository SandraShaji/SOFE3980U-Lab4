package com.ontariotechu.sofe3980U;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;

public class App {
    public static void main(String[] args) {
        String[] models = {"model_1.csv", "model_2.csv", "model_3.csv"};

        for (String modelFile : models) {
            // Path fix: check current directory or resources
            File file = new File(modelFile);
            if (!file.exists()) {
                file = new File("src/main/resources/" + modelFile);
            }

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> allData = reader.readAll();
                allData.remove(0); // Remove header

                double bce = 0;
                int tp = 0, fp = 0, tn = 0, fn = 0;
                int n = allData.size();
                double nPos = 0, nNeg = 0;

                // For ROC
                double[] x_roc = new double[101];
                double[] y_roc = new double[101];

                for (String[] line : allData) {
                    double actual = Double.parseDouble(line[0]);
                    double predicted = Double.parseDouble(line[1]);
                    
                    bce += actual * Math.log(predicted + 1e-10) + (1 - actual) * Math.log(1 - predicted + 1e-10);
                    
                    if (actual == 1) {
                        nPos++;
                        if (predicted >= 0.5) tp++; else fn++;
                    } else {
                        nNeg++;
                        if (predicted >= 0.5) fp++; else tn++;
                    }
                }

                for (int i = 0; i <= 100; i++) {
                    double th = i / 100.0;
                    double currentTP = 0, currentFP = 0;
                    for (String[] line : allData) {
                        double a = Double.parseDouble(line[0]);
                        double p = Double.parseDouble(line[1]);
                        if (a == 1 && p >= th) currentTP++;
                        if (a == 0 && p >= th) currentFP++;
                    }
                    y_roc[i] = (nPos > 0) ? currentTP / nPos : 0;
                    x_roc[i] = (nNeg > 0) ? currentFP / nNeg : 0;
                }

                double auc = 0;
                for (int i = 1; i <= 100; i++) {
                    auc += (y_roc[i-1] + y_roc[i]) * Math.abs(x_roc[i-1] - x_roc[i]) / 2.0;
                }

                double accuracy = (double)(tp + tn) / n;
                double precision = (tp + fp > 0) ? (double)tp / (tp + fp) : 0;
                double recall = (tp + fn > 0) ? (double)tp / (tp + fn) : 0;
                double f1 = (precision + recall > 0) ? 2 * (precision * recall) / (precision + recall) : 0;

                System.out.println("for " + modelFile);
                System.out.printf("\tBCE =%.7f\n", (-bce / n));
                System.out.println("\tConfusion matrix\n\t\t\ty=1\ty=0\n\t\ty^=1\t" + tp + "\t" + fp + "\n\t\ty^=0\t" + fn + "\t" + tn);
                System.out.printf("\tAccuracy =%.4f\n", accuracy);
                System.out.printf("\tPrecision =%.8f\n", precision);
                System.out.printf("\tRecall =%.8f\n", recall);
                System.out.printf("\tf1 score =%.8f\n", f1);
                System.out.printf("\tauc roc =%.8f\n", auc);

            } catch (Exception e) {
                System.err.println("Could not find file: " + modelFile);
            }
        }
    }
}