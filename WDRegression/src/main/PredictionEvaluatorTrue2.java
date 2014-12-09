package main;

import files.FReader;
import files.Misc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PredictionEvaluatorTrue2
  implements Evaluator
{
  FWriter writer3;
  private DataPrep pre;
  int cursor;
  int nRuns;
  String filename;
  int currentRun = 0;
  private int noOfMetrics = 1;
  double[][] summaryStats;
  double[] rawData;
  double[] rain_Tr;
  double[] rain_t130_Tr;
  double[] rain_t3160_Tr;
  double[] rain_t6190_Tr;
  double[] rain_t365_Tr;
  double[] rain_t730_Tr;
  double[] rain_Ts;
  double[] rain_t130_Ts;
  double[] rain_t3160_Ts;
  double[] rain_t6190_Ts;
  double[] rain_t365_Ts;
  double[] rain_t730_Ts;
  //double[]t_Tr;
  //double[]t_Ts;
  private double[] rain_Tr_t1;
  private double[] rain_Tr_t2;
  private double[] rain_Tr_t3;
  private double[] rain_Tr_t12;
  private double[] rain_Tr_t24;
  private double[] rain_Ts_t1;
  private double[] rain_Ts_t2;
  private double[] rain_Ts_t3;
  private double[] rain_Ts_t12;
  private double[] rain_Ts_t24;         
  public static String sumStat = "\tMSE\n";
  public static String sumStat2 = "";
  
  public PredictionEvaluatorTrue2(int nRuns, String filename, int contractLength)
  {
      
      
    this.nRuns = nRuns;
    this.filename = filename;
    
    String training = "Data/" + filename + "/Training.csv";
    String testing = "Data/" + filename + "/Testing.csv";
    
    int rows1 = 0;
    int cols1 = 0;
    int rows2 = 0;
    int cols2 = 0;
    try
    {
      rows1 = FReader.getRowLength(training);
      cols1 = FReader.getColumnLength(training);
      rows2 = FReader.getRowLength(testing);
      cols2 = FReader.getColumnLength(testing);
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
    
    
    double[][] data1 = new double[rows1][cols1];
    double[][] data2 = new double[rows2][cols2];    
    try
    {
      FReader.read(training, data1);
      FReader.read(testing, data2);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    
    rawData = Misc.copy(data1,0);
    
    rain_Tr = pre.predictCL(contractLength);
    rain_Tr_t1 = pre.lastCL(contractLength);
    rain_Tr_t2 = pre.lastTwoCL(contractLength);
    rain_Tr_t3 = pre.lastThreeCL(contractLength);
    rain_Tr_t12 = pre.lastYearCL(contractLength);
    rain_Tr_t24 = pre.twoYearCL(contractLength);
            
//    this.rain_Tr = Misc.copy(data1, 0);
//    this.rain_t130_Tr = Misc.copy(data1, 1);
//    this.rain_t3160_Tr = Misc.copy(data1, 2);
//    this.rain_t6190_Tr = Misc.copy(data1, 3);
//    this.rain_t365_Tr = Misc.copy(data1, 4);
//    this.rain_t730_Tr = Misc.copy(data1, 5);
//    this.t_Tr = Misc.copy(data1,6);
    
    rawData = Misc.copy(data2,0);
    
    rain_Ts = pre.predictCL(contractLength);
    rain_Ts_t1 = pre.lastCL(contractLength);
    rain_Ts_t2 = pre.lastTwoCL(contractLength);
    rain_Ts_t3 = pre.lastThreeCL(contractLength);
    rain_Ts_t12 = pre.lastYearCL(contractLength);
    rain_Ts_t24 = pre.twoYearCL(contractLength);
//    this.rain_Ts = Misc.copy(data2, 0);
//    this.rain_t130_Ts = Misc.copy(data2, 1);
//    this.rain_t3160_Ts = Misc.copy(data2, 2);
//    this.rain_t6190_Ts = Misc.copy(data2, 3);
//    this.rain_t365_Ts = Misc.copy(data2, 4);
//    this.rain_t730_Ts = Misc.copy(data2, 5);
//    this.t_Ts = Misc.copy(data1, 6);
    
    this.cursor = 0;
    File results = new File("./Results");
    results.mkdir();
    
    this.summaryStats = new double[nRuns][1];
    
    this.writer3 = new FWriter("Results/Summary Statistics.txt", this.cursor);
    this.writer3.writeLog("\tMSE\n");
    this.writer3.closeFile();
  }
  
  public double eval(Expr evolvedMethod)
  {
    double SE = 0.0D;
    for (int i = 0; i < this.rain_Tr.length; i++)
    {
      Variable var1 = new Variable(new Double(this.rain_t130_Tr[i]));
      Variable var2 = new Variable(new Double(this.rain_t3160_Tr[i]));
      Variable var3 = new Variable(new Double(this.rain_t6190_Tr[i]));
      Variable var4 = new Variable(new Double(this.rain_t365_Tr[i]));
      Variable var5 = new Variable(new Double(this.rain_t730_Tr[i]));
      //Variable var6 = new Variable(new Double(this.t_Tr[i]));
      
      Expr[] env = { var1, var2, var3, var4, var5 };
      Object o = evolvedMethod.eval(env);
      double prediction = ((Double)o).doubleValue();
      
      SE += Math.pow(prediction - this.rain_Tr[i], 2.0D);
    }
    double MSE = Arithmetic.sqrt(SE / this.rain_Tr.length);
    
    return MSE;
  }
  
  public double test(Expr evolvedMethod)
  {
    double SE = 0.0D;
    String predictions = "";
    for (int i = 0; i < this.rain_Ts.length; i++)
    {
      Variable var1 = new Variable(new Double(this.rain_t130_Ts[i]));
      Variable var2 = new Variable(new Double(this.rain_t3160_Ts[i]));
      Variable var3 = new Variable(new Double(this.rain_t6190_Ts[i]));
      Variable var4 = new Variable(new Double(this.rain_t365_Ts[i]));
      Variable var5 = new Variable(new Double(this.rain_t730_Ts[i]));
      //Variable var6 = new Variable(new Double(this.t_Ts[i]));
      
      Expr[] env = { var1, var2, var3, var4, var5 };
      Object o = evolvedMethod.eval(env);
      double prediction = ((Double)o).doubleValue();
      predictions = predictions + prediction + "\n";
      SE += Math.pow(prediction - this.rain_Ts[i], 2.0D);
    }
    double MSE = Arithmetic.sqrt(SE / this.rain_Ts.length);
    
    FWriter pred = new FWriter("Results/Experiment " + this.currentRun + "/Predictions" + this.currentRun + ".txt", this.cursor);
    pred.writeLog("Temperature Prediction\n");
    pred.writeLog(predictions + "\n");
    pred.closeFile();
    
    this.cursor += 1;
    
    sumStat = sumStat + "Run " + this.currentRun + "\t" + MSE + "\n";
    
    this.summaryStats[this.currentRun][0] = MSE;
    
    double[][] array = new double[4][this.noOfMetrics];
    if (this.currentRun == this.nRuns - 1)
    {
      this.writer3 = new FWriter("Results/Summary Statistics.txt", this.cursor);
      this.writer3.writeLog(sumStat);
      

      array[0] = ((double[])StatisticalSummary.calculateMean(this.summaryStats).clone());
      array[1] = ((double[])StatisticalSummary.calculateStDev(this.summaryStats).clone());
      array[2] = ((double[])StatisticalSummary.calculateMax(this.summaryStats).clone());
      array[3] = ((double[])StatisticalSummary.calculateMin(this.summaryStats).clone());
      this.writer3.writeLog("");
      this.writer3.writeLog2("Mean\t");
      for (int i = 0; i < this.noOfMetrics; i++) {
        this.writer3.writeLog2(array[0][i] + "\t");
      }
      this.writer3.writeLog("");
      this.writer3.writeLog2("Standard Deviation\t");
      for (int i = 0; i < this.noOfMetrics; i++) {
        this.writer3.writeLog2(array[1][i] + "\t");
      }
      this.writer3.writeLog("");
      this.writer3.writeLog2("Max\t");
      for (int i = 0; i < this.noOfMetrics; i++) {
        this.writer3.writeLog2(array[2][i] + "\t");
      }
      this.writer3.writeLog("");
      this.writer3.writeLog2("Min\t");
      for (int i = 0; i < this.noOfMetrics; i++) {
        this.writer3.writeLog2(array[3][i] + "\t");
      }
    }
    this.writer3.closeFile();
    
    String ar0 = "";
    String ar1 = "";
    String ar2 = "";
    String ar3 = "";
    for (int i = 0; i < this.noOfMetrics; i++) {
      ar0 = ar0 + (float)array[0][i] + "\t";
    }
    for (int i = 0; i < this.noOfMetrics; i++) {
      ar1 = ar1 + (float)array[1][i] + "\t";
    }
    for (int i = 0; i < this.noOfMetrics; i++) {
      ar2 = ar2 + (float)array[2][i] + "\t";
    }
    for (int i = 0; i < this.noOfMetrics; i++) {
      ar3 = ar3 + (float)array[3][i] + "\t";
    }
    sumStat2 = "\n\nMean" + ar0 + "\nStand. Dev.\t" + ar1 + "\nMax\t" + ar2 + "\nMin\t" + ar3;
    

    this.currentRun += 1;
    
    return MSE;
  }
}
