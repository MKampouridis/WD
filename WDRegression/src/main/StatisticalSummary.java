package main;

import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class StatisticalSummary
{
  static FileWriter fws = null;
  static PrintWriter out;
  int noOfGenerations;
  int noOfIndividuals;
  static double[][] imediateStats;
  double[][] summaryStats;
  double[][] summaryStatistics;
  String fileName = "";
  FWriter writer;
  static FWriter experimentSetupLogger = new FWriter("Results/Experiment_Setup.txt", 0);
  int experimentNo;
  
  public StatisticalSummary(int noOfGenerations, int noOfIndividuals)
  {
    this.noOfGenerations = noOfGenerations;
    this.noOfIndividuals = noOfIndividuals;
    imediateStats = new double[noOfGenerations][2];
    this.summaryStats = new double[noOfGenerations][9];
    this.summaryStatistics = new double[noOfGenerations][9];
  }
  
  public StatisticalSummary(int noOfGenerations, int noOfIndividuals, int cursor)
  {
    this.noOfGenerations = noOfGenerations;
    this.noOfIndividuals = noOfIndividuals;
    imediateStats = new double[noOfGenerations][2];
    this.summaryStats = new double[noOfGenerations][9];
    this.summaryStatistics = new double[noOfGenerations][9];
    this.fileName = ("/log" + cursor + ".txt");
    this.writer = new FWriter("Results/Experiment " + cursor + "/" + fileName, cursor);
    this.experimentNo = cursor;
  }
  
  public void addStat(Object[] pop, int index)
  {
    Object[] popCopy = new Object[pop.length];
    for (int i = 0; i < popCopy.length; i++) {
      popCopy[i] = pop[i];
    }
    Arrays.sort(popCopy);
    
    Object[] Fitness = new Object[this.noOfIndividuals];
    for (int i = 0; i < this.noOfIndividuals; i++) {
      Fitness[i] = ((Evaluated)pop[i]).fitness;
    }
    Arrays.sort(Fitness);
    System.out.println("Best fitness: " + Fitness[0]);
    double mean = calculateMean(Fitness);
    imediateStats[index][0] = mean;
    imediateStats[index][1] = ((Double)Fitness[0]).doubleValue();
  }
  
  public static void logExperimentSetup(ArrayList<?> functions, ArrayList<?> terminals, int maxInitDepth, int maxDepth, double primProb, double terminalNodeCrossBias, int nGens, int popSize, int tournamentSize, double mutProb, double crossoverProbability)
  {
    experimentSetupLogger.writeLog("FUNCTION SET:");
    experimentSetupLogger.writeLog(functions.toString());
    experimentSetupLogger.writeLog("TERMINAL SET:");
    experimentSetupLogger.writeLog(terminals.toString() + "\n");
    experimentSetupLogger.writeLog("EXPERIMENT PARAMETERS:");
    experimentSetupLogger.writeLog("Maximum initial depth: " + maxInitDepth);
    experimentSetupLogger.writeLog("Maximum depth: " + maxDepth);
    experimentSetupLogger.writeLog("Primitive probability in Grow method: " + primProb);
    experimentSetupLogger.writeLog("Terminal node crossover bias: " + terminalNodeCrossBias);
    experimentSetupLogger.writeLog("No of generations: " + nGens);
    experimentSetupLogger.writeLog("Population size: " + popSize);
    experimentSetupLogger.writeLog("Tournament size: " + tournamentSize);
    
    experimentSetupLogger.writeLog("Crossover probability: " + crossoverProbability);
    experimentSetupLogger.writeLog("Reproduction probability: " + (1.0D - crossoverProbability));
    experimentSetupLogger.writeLog("Mutation probability: " + mutProb);
    experimentSetupLogger.closeFile();
  }
  
  public double calculateMean(Object[] arr)
  {
    double mean = 0.0D;
    for (int i = 0; i < arr.length; i++)
    {
      Double current = (Double)arr[i];
      mean += current.doubleValue();
    }
    return mean / arr.length;
  }
  
  public static double[] calculateMean(double[][] arr)
  {
    double[] mean = new double[arr[0].length];
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++)
      {
        Double current = Double.valueOf(arr[i][j]);
        mean[j] += current.doubleValue();
      }
    }
    for (int j = 0; j < arr[0].length; j++) {
      mean[j] /= arr.length;
    }
    return mean;
  }
  
  public static double[] calculateMax(double[][] arr)
  {
    double[] max = new double[arr[0].length];
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++)
      {
        Double current = Double.valueOf(arr[i][j]);
        if (current.doubleValue() >= max[j]) {
          max[j] = current.doubleValue();
        }
      }
    }
    return max;
  }
  
  public static double[] calculateMin(double[][] arr)
  {
    double[] min = new double[arr[0].length];
    for (int i = 0; i < min.length; i++) {
      min[i] = arr[0][i];
    }
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++)
      {
        Double current = Double.valueOf(arr[i][j]);
        if (current.doubleValue() <= min[j]) {
          min[j] = current.doubleValue();
        }
      }
    }
    return min;
  }
  
  public static double[] calculateStDev(double[][] arr)
  {
    double[] stDev = { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D };
    double[] sqrSum = { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D };
    double[] array = (double[])calculateMean(arr).clone();
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++) {
        sqrSum[j] += Math.pow(arr[i][j] - array[j], 2.0D);
      }
    }
    for (int i = 0; i < stDev.length; i++)
    {
      sqrSum[i] /= (arr.length - 1);
      stDev[i] = Math.sqrt(sqrSum[i]);
    }
    return stDev;
  }
  
  public void logStatisticalSummary(int genNo)
  {
    this.writer.writeLog("Average Fitness \t  Best Individual");
    for (int i = 0; i <= genNo; i++)
    {
      this.writer.writeLog(imediateStats[i][0] + "\t " + imediateStats[i][1]);
      for (int j = 0; j < imediateStats[i].length; j++) {
        this.summaryStatistics[i][j] = imediateStats[i][j];
      }
    }
    this.writer.closeFile();
  }
}
