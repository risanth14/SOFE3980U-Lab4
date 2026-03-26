package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        float epsilon = 0.0000001f;

        float bestMSE = Float.MAX_VALUE;
        float bestMAE = Float.MAX_VALUE;
        float bestMARE = Float.MAX_VALUE;

        String bestMSEModel = "";
        String bestMAEModel = "";
        String bestMAREModel = "";

        for (String filePath : files)
        {
            List<String[]> allData;

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
                System.out.println("Error reading the CSV file: " + filePath);
                return;
            }

            float sumSquaredError = 0.0f;
            float sumAbsoluteError = 0.0f;
            float sumAbsoluteRelativeError = 0.0f;

            for (String[] row : allData)
            {
                float y_true = Float.parseFloat(row[0]);
                float y_predicted = Float.parseFloat(row[1]);

                float error = y_true - y_predicted;

                sumSquaredError += error * error;
                sumAbsoluteError += Math.abs(error);
                sumAbsoluteRelativeError += Math.abs(error) / (Math.abs(y_true) + epsilon);
            }

            float mse = sumSquaredError / allData.size();
            float mae = sumAbsoluteError / allData.size();
            float mare = sumAbsoluteRelativeError / allData.size();

            System.out.println("for " + filePath);
            System.out.println("\tMSE =" + mse);
            System.out.println("\tMAE =" + mae);
            System.out.println("\tMARE =" + mare);

            if (mse < bestMSE)
            {
                bestMSE = mse;
                bestMSEModel = filePath;
            }

            if (mae < bestMAE)
            {
                bestMAE = mae;
                bestMAEModel = filePath;
            }

            if (mare < bestMARE)
            {
                bestMARE = mare;
                bestMAREModel = filePath;
            }
        }

        System.out.println("According to MSE, The best model is " + bestMSEModel);
        System.out.println("According to MAE, The best model is " + bestMAEModel);
        System.out.println("According to MARE, The best model is " + bestMAREModel);
    }
}