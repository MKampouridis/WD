package main;

import files.FReader;
import files.Misc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PredictionEvaluatorTrue2
  implements Evaluator
{
  FReader read = new FReader();  
  FWriter writer3;
  int cursor = 0;
  int nRuns;
  String filename;
  int currentRun = 0;
  private int noOfMetrics = 1;
  double[][] summaryStats;
  private int rows1;
  private int rows2;
  private int cols;
  //double[]t_Tr;
  //double[]t_Ts;         
  public static String sumStat = "\tMSE\n";
  public static String sumStat2 = "";
  Variable var;
  Expr[] env;
  Map<Integer,double[]> rain_tr = new HashMap<>();
  Map<Integer,double[]> rain_ts = new HashMap<>();
  double[] temp;
  public PredictionEvaluatorTrue2(int nRuns, String filename, int contractLength)
  {
      
    this.currentRun = 0;  
    this.nRuns = nRuns;
    this.filename = filename;
    
    //Old data
    // String training = "Data/" + Run.totalY + "/" + Run.totalT + "/luxTraining"+Run.totalYears+"_"+Run.contractLength+".csv";
    //Commented out as not needed for now, will avoid possible errors String testing = "Data/"  + Run.totalY + "/" + Run.totalT + "/luxTesting1_"+Run.contractLength+".csv";
    
    //data with lots of params, access of files is dependent on MA
    String training = "Data/MA" + Run.movingAverage + "/luxTraining" + Run.yrs + "_" + Run.spreadYrs + "_MA" + Run.movingAverage + "_" + Run.contractLength;
    rows1 = 0;
    cols = 0;
    rows2 = 0;
    
    try
    {
      rows1 = read.getRowLength(training) -1; //minus 1 because the data includes headers
      cols = read.getColumnLength(training);
      //Commented out as not needed for now, will avoid possible errors rows2 = read.getRowLength(testing) -1; //minus 1 because the data includes headers
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
    
    env = new Expr[cols];
    
    double[][] data1 = new double[rows1][cols];
    double[][] data2 = new double[rows2][cols];
    read.readArray(training, data1, cols);
    //Commented out as not needed for now, will avoid possible errors read.readArray(testing,data2, cols);
    //System.out.println(rows1);
    if (Run.splitData == false) {
    //System.out.println("Hi are you here?");
    rain_tr.put(0, Misc.copy(data1, 0));
    for(int i = 1; i < cols; i++){
        rain_tr.put(i, Misc.copy(data1, i));    
    }
    //System.out.println(rain_tr.size());
    
    //System.out.println(Arrays.toString(rain_tr.get(0)));
        
    rain_ts.put(0, Misc.copy(data2, 0));
    for(int i = 1; i < cols; i++){
        rain_ts.put(i, Misc.copy(data2, i));
    }
    
    } else if (Run.splitData == true && Run.randomData == true) { //This will randomly select variables within training data in a mixed order
        // This may need to be moved to a separate method if training set is required to be recalculated every generation
        //System.out.println("Hi are you hereeee?");
        double[][] data3 = new double[data1.length][cols];
        double[][] data4 = new double[data1.length][cols];
                
        int counta = 0;
        int countb = 0;
        for (int i = 0; i < data1.length; i++) {
            Random r = new Random();
            if (r.nextDouble() >= Run.splitPercent) {
                for(int j = 0; j < cols; j++) {
                    data4[i][j] = data1[i][j];
                    countb++;
                }
            } else {
                for(int j = 0; j < cols; j++) {
                    data3[i][j] = data1[i][j];
                    counta++;
                }
            }
        }
        data3 = Arrays.copyOf(data3, counta);
        data4 = Arrays.copyOf(data4, countb);
        rows1 = data3.length;
        rows2 = data4.length;
        rain_tr.put(0, Misc.copy(data3, 0));
        for(int i = 1; i < cols; i++){
            rain_tr.put(i, Misc.copy(data3, i));    
        }
    
        rain_ts.put(0, Misc.copy(data4, 0));
        for(int i = 1; i < cols; i++){
            rain_ts.put(i, Misc.copy(data4, i));
        }    
    } else { //separate training into a fixed partition according to split percentage
                
        //int sizeOfArray = (int)(data1.length * Run.splitPercent) + 1; //used if you want a percentage split of data
        int sizeOfArray = (data1.length - 365); //used if you want a certain number of periods
        System.out.println(data1.length);
        //System.out.println(Run.splitPercent);
        System.out.println(sizeOfArray);
        double[][] data3 = Arrays.copyOfRange(data1, 0, sizeOfArray);
        double[][] data4 = Arrays.copyOfRange(data1, sizeOfArray, data1.length);
        rows1 = data3.length;
        rows2 = data4.length;
        System.out.println(rows1);
        System.out.println(rows2);
        rain_tr.put(0, Misc.copy(data3, 0));
        for(int i = 1; i < cols; i++){
            rain_tr.put(i, Misc.copy(data3, i));    
        }
        
        rain_ts.put(0, Misc.copy(data4, 0));
        for(int i = 1; i < cols; i++){
            rain_ts.put(i, Misc.copy(data4, i));
        }
        
    }
    File results = new File("./"+Run.filenameS);
    results.mkdir();
   
    this.summaryStats = new double[nRuns][1];
    File sumStats = new File ("./SummaryStats/Results/");
    sumStats.mkdirs();
    this.writer3 = new FWriter("SummaryStats/" + Run.filenameS+"_SummaryStatistics.txt", this.cursor);
    this.writer3.writeLog("\tMSE\n");
    this.writer3.closeFile();
    
    
  }
  
  public double eval(Expr evolvedMethod)
  {
      
    double SE = 0.0D;
    for (int i = 0; i < rows1; i++)
    {
        for (int j = 1; j < cols; j++) {
            temp = rain_tr.get(j);
            env[j-1] = new Variable(temp[i]);
      }
      
      Object o = evolvedMethod.eval(env);
      double prediction = ((Double)o).doubleValue();
      if(prediction < 0) { 
          prediction = 0;
      }
      temp = rain_tr.get(0);
      SE += Math.pow(prediction - temp[i], 2.0D);
    }
    double MSE = Arithmetic.sqrt(SE / rows1);
    
    return MSE;
  }
  
  public double test(Expr evolvedMethod)
  {
    double SE = 0.0D;
    String predictions = "";
    for (int i = 0; i < rows2; i++)
    {
        for (int j = 1; j < cols; j++) {
            temp = rain_tr.get(j);
            env[j-1] = new Variable(temp[i]);
        }
      Object o = evolvedMethod.eval(env);
      double prediction = ((Double)o).doubleValue();
      if(prediction < 0) { 
          prediction = 0;
      }
      predictions = predictions + prediction + "\n";
      SE += Math.pow(prediction - temp[i], 2.0D);
      
    }
    double MSE = Arithmetic.sqrt(SE / rows2);
    
    FWriter pred = new FWriter(Run.filenameS+"/Experiment " + this.currentRun + "/Predictions" + this.currentRun + ".txt", this.cursor);
    pred.writeLog("Rainfall Prediction\n");
    pred.writeLog(predictions + "\n");
    pred.closeFile();
    
    this.cursor += 1;
    
    sumStat = sumStat + "Run " + this.currentRun + "\t" + MSE + "\n";
    
    this.summaryStats[this.currentRun][0] = MSE;
    
    double[][] array = new double[4][this.noOfMetrics];
    if (this.currentRun == this.nRuns - 1)
    {
      this.writer3 = new FWriter("SummaryStats/" + Run.filenameS+"_SummaryStatistics.txt", this.cursor);
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
    System.out.println(currentRun);
    if (currentRun == Run.nRuns) {
        System.out.println("Average fitness: " + ar0);
    }
    return MSE;
  }
}
