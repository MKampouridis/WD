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
  int cursor;
  int nRuns;
  String filename;
  int currentRun = 0;
  private int noOfMetrics = 1;
  double[][] summaryStats;
  double[] rain_Tr;
  double[] rain_t1_Tr;
  double[] rain_t2_Tr;
  double[] rain_t3_Tr;
  double[] rain_t4_Tr;
  double[] rain_t5_Tr;
  double[] rain_t6_Tr;
  double[] rain_t7_Tr;
  double[] rain_t8_Tr;
  double[] rain_Ts;
  double[] rain_t1_Ts;
  double[] rain_t2_Ts;
  double[] rain_t3_Ts;
  double[] rain_t4_Ts;
  double[] rain_t5_Ts;
  double[] rain_t6_Ts;
  double[] rain_t7_Ts;
  double[] rain_t8_Ts;
  double[] r_Tr;
  double[] r_Ts;
  public static String sumStat = "\tMSE\n";
  public static String sumStat2 = "";
  
  public PredictionEvaluatorTrue2(int nRuns, String filename)
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
    this.rain_Tr = Misc.copy(data1, 0);
    this.rain_t1_Tr = Misc.copy(data1, 1);
    this.rain_t2_Tr = Misc.copy(data1, 2);
    this.rain_t3_Tr = Misc.copy(data1, 3);
    this.rain_t4_Tr = Misc.copy(data1, 4);
    this.rain_t5_Tr = Misc.copy(data1, 5);
    this.rain_t6_Tr = Misc.copy(data1, 6);
    this.rain_t7_Tr = Misc.copy(data1, 7);
    this.rain_t8_Tr = Misc.copy(data1, 8);
    this.rain_Ts = Misc.copy(data2, 0);
    this.rain_t1_Ts = Misc.copy(data2, 1);
    this.rain_t2_Ts = Misc.copy(data2, 2);
    this.rain_t3_Ts = Misc.copy(data2, 3);
    this.rain_t4_Ts = Misc.copy(data2, 4);
    this.rain_t5_Ts = Misc.copy(data2, 5);
    this.rain_t6_Ts = Misc.copy(data2, 6);
    this.rain_t7_Ts = Misc.copy(data2, 7);
    this.rain_t8_Ts = Misc.copy(data2, 8);
    this.r_Tr = Misc.copy(data1, 9);
    this.r_Ts = Misc.copy(data2, 9);
    
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
      Variable var1 = new Variable(new Double(this.rain_t1_Tr[i]));
      Variable var2 = new Variable(new Double(this.rain_t2_Tr[i]));
      Variable var3 = new Variable(new Double(this.rain_t3_Tr[i]));
      Variable var4 = new Variable(new Double(this.rain_t4_Tr[i]));
      Variable var5 = new Variable(new Double(this.rain_t5_Tr[i]));
      Variable var6 = new Variable(new Double(this.rain_t6_Tr[i]));
      Variable var7 = new Variable(new Double(this.rain_t7_Tr[i]));
      Variable var8 = new Variable(new Double(this.rain_t8_Tr[i]));
      Variable var9 = new Variable(new Double(this.r_Tr[i]));
      
      Expr[] env = { var1, var2, var3, var4, var5, var6, var7, var8, var9 };
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
      Variable var1 = new Variable(new Double(this.rain_t1_Ts[i]));
      Variable var2 = new Variable(new Double(this.rain_t2_Ts[i]));
      Variable var3 = new Variable(new Double(this.rain_t3_Ts[i]));
      Variable var4 = new Variable(new Double(this.rain_t4_Ts[i]));
      Variable var5 = new Variable(new Double(this.rain_t5_Ts[i]));
      Variable var6 = new Variable(new Double(this.rain_t6_Ts[i]));
      Variable var7 = new Variable(new Double(this.rain_t7_Ts[i]));
      Variable var8 = new Variable(new Double(this.rain_t8_Ts[i]));
      Variable var9 = new Variable(new Double(this.r_Ts[i]));
      
      Expr[] env = { var1, var2, var3, var4, var5, var6, var7, var8, var9 };
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
