package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Multi Class Classification
 *
 */
public class App
{
    public static void main(String[] args)
    {
        String filePath = "model.csv";
        List<String[]> allData;
        double epsilon = 0.0000001;

        try
        {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
            csvReader.close();
            filereader.close();
        }
        catch (Exception e)
        {
            System.out.println("Error reading the CSV file");
            return;
        }

        double crossEntropySum = 0.0;
        int[][] confusionMatrix = new int[5][5];

        for (String[] row : allData)
        {
            int y_true = Integer.parseInt(row[0]);
            double[] y_predicted = new double[5];

            int predictedClass = 1;
            double maxProbability = -1.0;

            for (int i = 0; i < 5; i++)
            {
                y_predicted[i] = Double.parseDouble(row[i + 1]);

                if (y_predicted[i] > maxProbability)
                {
                    maxProbability = y_predicted[i];
                    predictedClass = i + 1;
                }
            }

            double trueClassProbability = y_predicted[y_true - 1];
            if (trueClassProbability < epsilon)
            {
                trueClassProbability = epsilon;
            }

            crossEntropySum += -Math.log(trueClassProbability);
            confusionMatrix[predictedClass - 1][y_true - 1]++;
        }

        double ce = crossEntropySum / allData.size();

        System.out.println("CE =" + ce);
        System.out.println("Confusion matrix");
        System.out.printf("%8s%8s%8s%8s%8s%8s%n", "", "y=1", "y=2", "y=3", "y=4", "y=5");

        for (int i = 0; i < 5; i++)
        {
            System.out.printf("%8s", "y^=" + (i + 1));
            for (int j = 0; j < 5; j++)
            {
                System.out.printf("%8d", confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}