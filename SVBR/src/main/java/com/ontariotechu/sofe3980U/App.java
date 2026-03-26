package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Binary Regression
 *
 */
public class App
{
    public static void main(String[] args)
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        double epsilon = 0.0000001;

        double bestBCE = Double.MAX_VALUE;
        double bestAccuracy = -1.0;
        double bestPrecision = -1.0;
        double bestRecall = -1.0;
        double bestF1 = -1.0;
        double bestAUC = -1.0;

        String bestBCEModel = "";
        String bestAccuracyModel = "";
        String bestPrecisionModel = "";
        String bestRecallModel = "";
        String bestF1Model = "";
        String bestAUCModel = "";

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

            double sumBCE = 0.0;
            int tp = 0;
            int fp = 0;
            int fn = 0;
            int tn = 0;

            int positiveCount = 0;
            int negativeCount = 0;

            ArrayList<double[]> rocData = new ArrayList<double[]>();

            for (String[] row : allData)
            {
                int y_true = Integer.parseInt(row[0]);
                double y_predicted = Double.parseDouble(row[1]);

                if (y_predicted < epsilon)
                {
                    y_predicted = epsilon;
                }
                if (y_predicted > 1.0 - epsilon)
                {
                    y_predicted = 1.0 - epsilon;
                }

                sumBCE += -(y_true * Math.log(y_predicted) + (1 - y_true) * Math.log(1.0 - y_predicted));

                int predictedClass;
                if (y_predicted >= 0.5)
                {
                    predictedClass = 1;
                }
                else
                {
                    predictedClass = 0;
                }

                if (y_true == 1)
                {
                    positiveCount++;
                }
                else
                {
                    negativeCount++;
                }

                if (predictedClass == 1 && y_true == 1)
                {
                    tp++;
                }
                else if (predictedClass == 1 && y_true == 0)
                {
                    fp++;
                }
                else if (predictedClass == 0 && y_true == 1)
                {
                    fn++;
                }
                else
                {
                    tn++;
                }

                rocData.add(new double[]{y_predicted, y_true});
            }

            double bce = sumBCE / allData.size();
            double accuracy = (double)(tp + tn) / allData.size();

            double precision = 0.0;
            if ((tp + fp) != 0)
            {
                precision = (double)tp / (tp + fp);
            }

            double recall = 0.0;
            if ((tp + fn) != 0)
            {
                recall = (double)tp / (tp + fn);
            }

            double f1 = 0.0;
            if ((precision + recall) != 0)
            {
                f1 = 2.0 * precision * recall / (precision + recall);
            }

            Collections.sort(rocData, new Comparator<double[]>() {
                public int compare(double[] a, double[] b)
                {
                    return Double.compare(b[0], a[0]);
                }
            });

            int runningTP = 0;
            int runningFP = 0;
            double previousTPR = 0.0;
            double previousFPR = 0.0;
            double auc = 0.0;

            int i = 0;
            while (i < rocData.size())
            {
                double currentScore = rocData.get(i)[0];
                int addedTP = 0;
                int addedFP = 0;

                while (i < rocData.size() && rocData.get(i)[0] == currentScore)
                {
                    if ((int)rocData.get(i)[1] == 1)
                    {
                        addedTP++;
                    }
                    else
                    {
                        addedFP++;
                    }
                    i++;
                }

                runningTP += addedTP;
                runningFP += addedFP;

                double tpr = 0.0;
                if (positiveCount != 0)
                {
                    tpr = (double)runningTP / positiveCount;
                }

                double fpr = 0.0;
                if (negativeCount != 0)
                {
                    fpr = (double)runningFP / negativeCount;
                }

                auc += (fpr - previousFPR) * (tpr + previousTPR) / 2.0;

                previousTPR = tpr;
                previousFPR = fpr;
            }

            System.out.println("for " + filePath);
            System.out.println("\tBCE =" + bce);
            System.out.println("\tConfusion matrix");
            System.out.println("\t\t\ty=1\t\ty=0");
            System.out.println("\t\ty^=1\t" + tp + "\t" + fp);
            System.out.println("\t\ty^=0\t" + fn + "\t" + tn);
            System.out.println("\tAccuracy =" + accuracy);
            System.out.println("\tPrecision =" + precision);
            System.out.println("\tRecall =" + recall);
            System.out.println("\tf1 score =" + f1);
            System.out.println("\tauc roc =" + auc);

            if (bce < bestBCE)
            {
                bestBCE = bce;
                bestBCEModel = filePath;
            }

            if (accuracy > bestAccuracy)
            {
                bestAccuracy = accuracy;
                bestAccuracyModel = filePath;
            }

            if (precision > bestPrecision)
            {
                bestPrecision = precision;
                bestPrecisionModel = filePath;
            }

            if (recall > bestRecall)
            {
                bestRecall = recall;
                bestRecallModel = filePath;
            }

            if (f1 > bestF1)
            {
                bestF1 = f1;
                bestF1Model = filePath;
            }

            if (auc > bestAUC)
            {
                bestAUC = auc;
                bestAUCModel = filePath;
            }
        }

        System.out.println("According to BCE, The best model is " + bestBCEModel);
        System.out.println("According to Accuracy, The best model is " + bestAccuracyModel);
        System.out.println("According to Precision, The best model is " + bestPrecisionModel);
        System.out.println("According to Recall, The best model is " + bestRecallModel);
        System.out.println("According to F1 score, The best model is " + bestF1Model);
        System.out.println("According to AUC ROC, The best model is " + bestAUCModel);
    }
}