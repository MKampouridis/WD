package main;

import files.FReader;
import files.Misc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.apache.commons.math3.distribution.GammaDistribution;

public class PredictionEvaluatorTrue2
  implements Evaluator
{
    private int counterMin;
    private int counterMax;
  GammaDistribution gam;
  FReader read = new FReader();  
  FWriter writer3;
  private double gamR;
  private double[] gamCoeff = new double[2];
  private int cursor = 0;
  private int nRuns;
  private int currentRun = 0;
  private int noOfMetrics = 1;
  private double[][] summaryStats;
  private int rows1;
  private int rows2;
  private int cols;
  private double[] predT;
  private double[] minT;
  private double[] maxT;
  private double[] diffMinT;
  private double[] diffMaxT;
  private double[] listGamMin;
  private double[] listGamMax;
  private double[] listGamMinDiff;
  private double[] listGamMaxDiff;
  private double meanOfGam;
  private double meanOfMin;
  private double meanOfMax;
  private int additionalParameters;
  private int totalParams;
  //double[]t_Tr;
  //double[]t_Ts;         
  public static String sumStat = "\tMSE\n";
  public static String sumStat2 = "";
  private Variable var;
  private Expr[] env;
  Map<Integer,double[]> rain_tr = new HashMap<>();
  Map<Integer,double[]> rain_ts = new HashMap<>();
  public double[] temp;
  public ArrayList<Integer> parameterIndex;
  public int headerLength;
  public int flag = 0;
  
  public PredictionEvaluatorTrue2(int nRuns, int contractLength, ArrayList<Integer> parameterIndex, int headerLength, int additionalParameters)
  {
      this.additionalParameters = additionalParameters;
   this.parameterIndex=parameterIndex;
   this.headerLength = headerLength;
    this.currentRun = 0;  
    this.nRuns = nRuns;
    int[] tempParameterIndex = new int[parameterIndex.size()];
    Iterator<Integer> iterator = parameterIndex.iterator();
    for (int i = 0; i < tempParameterIndex.length; i++)
    {
        tempParameterIndex[i] = iterator.next();
    }
    totalParams = headerLength + additionalParameters;
    //Old data
    // String training = "Data/" + Run.totalY + "/" + Run.totalT + "/luxTraining"+Run.totalYears+"_"+Run.contractLength+".csv";
    //Commented out as not needed for now, will avoid possible errors String testing = "Data/"  + Run.totalY + "/" + Run.totalT + "/luxTesting1_"+Run.contractLength+".csv";
    
    //data with lots of params, access of files is dependent on MA
    String training = "Data/MA" + Run.movingAverage + "/"+Run.dataSet+"Training" + Run.yrs + "_" + Run.spreadYrs + "_MA" + Run.movingAverage + "_" + Run.contractLength + ".csv";
    rows1 = 0;
    cols = 0;
    rows2 = 0;
    
    try
    {
      rows1 = read.getRowLength(training) -1; //minus 1 because the data includes headers
      cols = parameterIndex.size();
      //cols = read.getColumnLength(training);
      //Commented out as not needed for now, will avoid possible errors rows2 = read.getRowLength(testing) -1; //minus 1 because the data includes headers
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
    
    env = new Expr[cols-1];
    
    
    double[][] data1 = new double[rows1][headerLength]; //based on headerLength as we are reading from array
    double[][] data2 = new double[rows2][cols];    
    
    read.readArray(training, data1, headerLength);
    //Commented out as not needed for now, will avoid possible errors read.readArray(testing,data2, cols);
    //System.out.println(rows1);
    if (Run.splitData == false) {
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
        double[][] data3 = Arrays.copyOfRange(data1, 0, sizeOfArray - Run.contractLength + 1);
        double[][] data4 = Arrays.copyOfRange(data1, sizeOfArray, data1.length);
        rows1 = data3.length;
        rows2 = data4.length;
        System.out.println(rows1);
        System.out.println(rows2);
        rain_tr.put(0, Misc.copy(data3, 0));
        rain_ts.put(0, Misc.copy(data4, 0));
        temp = rain_tr.get(0);
        for (int i = 1; i < cols; i++) {
            if (parameterIndex.get(i) < headerLength) {
                rain_tr.put(i, Misc.copy(data3, tempParameterIndex[i]));    
                rain_ts.put(i, Misc.copy(data4, tempParameterIndex[i]));
            } else {
                flag = 1; // turn flag to 1, meaning that we require a variable, caculated later
                //do nothing, parameters will be added in later, this will avoid null pointer.
            }                      
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
    
    //createEnv(); //down here as rows1 and rows2 are correct length
    
    //will look into this later, the problem I have is the updating of y-pred
  }
  
    public void gammaCheck(int train) {
        String[] dataSet = read.readHeader("Data/predGamma.txt");
        int index = 0;
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i].matches(Run.dataSet)) {
                index = i;
            }
        }
        read.readCol1D("Data/predGamma.csv",gamCoeff, index);
        gam = new GammaDistribution(gamCoeff[0], gamCoeff[1]);
        meanOfGam = 0;
        double sumOfgam = 0;
        for (int i = 0; i < 1000; i++) {            
            sumOfgam += gam.sample();
        }
        meanOfGam = sumOfgam/1000; //used for diff
        double gamSample = gam.sample();
        if (train == 1) {
            predT = new double[rows1];
        } else {
            predT = new double[rows2];
        }
        predT[0] = gamSample;
        if (parameterIndex.contains(headerLength)) {            
            if (train==1) {
                rain_tr.put(parameterIndex.indexOf(headerLength), predT);
            } else {
                rain_ts.put(parameterIndex.indexOf(headerLength), predT);
            }
        } else {
        //do nothing
        }
        if (Collections.max(parameterIndex) > headerLength) {
            if (train == 1) {
                minT = new double[rows1];
                maxT = new double[rows1];
            } else {
                minT = new double[rows2];
                maxT = new double[rows2];
            }
            minT[0] = gamSample;
            maxT[0] = gamSample;
            double gamCheck;    
            for (int i=0; i<4; i++) {
                gamCheck = gam.sample();
                if (gamCheck < minT[0]) {
                    minT[0] = gamCheck;
                } else {
                    maxT[0] = gamCheck;
                }
            }
            if (parameterIndex.contains(headerLength + 1)) {
                if (train==1) {
                    rain_tr.put(parameterIndex.indexOf(headerLength + 1), minT);
                } else {
                    rain_ts.put(parameterIndex.indexOf(headerLength + 1), minT);
                }
            } else {
                //do nothing
            }
            if (parameterIndex.contains(headerLength + 2)) {
                if (train==1) {
                    rain_tr.put(parameterIndex.indexOf(headerLength + 2), maxT);
                } else {
                    rain_ts.put(parameterIndex.indexOf(headerLength + 2), maxT);
                }
            }
            if (Collections.max(parameterIndex) > headerLength + 2) {
                if (train == 1) {
                    diffMinT = new double[rows1];
                    diffMaxT = new double[rows1];
                } else {
                    diffMinT = new double[rows2];
                    diffMaxT = new double[rows2];
                }
                diffMinT[0] = minT[0] - meanOfGam;
                diffMaxT[0] = maxT[0] - meanOfGam;
                
                if (parameterIndex.contains(headerLength + 3)) {
                    if (train==1) {
                        rain_tr.put(parameterIndex.indexOf(headerLength + 3), diffMinT);
                    } else {
                        rain_ts.put(parameterIndex.indexOf(headerLength + 3), diffMinT);
                    }
                } else {
                    //do nothing
                }
                if (parameterIndex.contains(headerLength + 4)) {
                    if (train==1) {
                        rain_tr.put(parameterIndex.indexOf(headerLength + 4), diffMaxT);
                    } else {
                        rain_ts.put(parameterIndex.indexOf(headerLength + 4), diffMaxT);
                    }
                } else {
                    //do nothing
                }
                
                if (Collections.max(parameterIndex) > headerLength + 4) {
                    double minCL = gamSample;
                    double maxCL = gamSample;
                    if (train == 1) {
                        listGamMin = new double[rows1];
                        listGamMax = new double[rows1];
                    } else {
                        listGamMin = new double[rows2];
                        listGamMax = new double[rows2];
                    }

                    double sumOfMinGam = 0;
                    double sumOfMaxGam = 0;
                    meanOfMin = 0;
                    meanOfMax = 0;

                    for (int j=0; j<Run.contractLength; j++) {
                        for (int i=0; i<10; i++) {
                            gamCheck = gam.sample();
                            if (gamCheck < minCL) {
                                minCL = gamCheck;
                                if (minCL < 5) {
                                    minCL = 5;
                                }
                            } else {
                                maxCL = gamCheck;
                            }
                        }
                        sumOfMinGam += minCL;
                        sumOfMaxGam += maxCL;
                        listGamMin[j] = minCL;
                        listGamMax[j] = maxCL;
                    }
                    if (parameterIndex.contains(headerLength + 5)) {
                        if (train==1) {
                            rain_tr.put(parameterIndex.indexOf(headerLength + 5), listGamMin);
                        } else {
                            rain_ts.put(parameterIndex.indexOf(headerLength + 5), listGamMin);
                        }
                    } else {
                        //do nothing
                    }
                    if (parameterIndex.contains(headerLength + 6)) {
                        if (train==1) {
                            rain_tr.put(parameterIndex.indexOf(headerLength + 6), listGamMax);
                        } else {
                            rain_ts.put(parameterIndex.indexOf(headerLength + 6), listGamMax);
                        }
                    } else {
                        //do nothing
                    }
                    if (Collections.max(parameterIndex) > headerLength + 6) {
                        meanOfMin = sumOfMinGam / Run.contractLength;
                        meanOfMax = sumOfMaxGam / Run.contractLength;
                        if (train == 1) {
                            listGamMinDiff = new double[rows1];
                            listGamMaxDiff = new double[rows1];
                        } else {
                            listGamMinDiff = new double[rows2];
                            listGamMaxDiff = new double[rows2];
                        }
                        for (int i = 0; i < Run.contractLength; i++) {
                            listGamMinDiff[i] = listGamMin[i] - meanOfMin;
                            listGamMaxDiff[i] = listGamMax[i] - meanOfMax;
                        }
                        if (parameterIndex.contains(headerLength + 7)) {
                            if (train==1) {
                                rain_tr.put(parameterIndex.indexOf(headerLength + 7), listGamMinDiff);
                            } else {
                                rain_ts.put(parameterIndex.indexOf(headerLength + 7), listGamMinDiff);
                            }
                        } else {
                            //do nothing
                        }
                        if (parameterIndex.contains(headerLength + 8)) {
                            if (train==1) {
                                rain_tr.put(parameterIndex.indexOf(headerLength + 8), listGamMaxDiff);
                            } else {
                                rain_ts.put(parameterIndex.indexOf(headerLength + 8), listGamMaxDiff);
                            }
                        } else {
                            //do nothing
                        }
                    }
                }
            }
        }
    }
    
    private void updateYpred(int train, double prediction, int index) {
        if (parameterIndex.contains(headerLength)) {
            predT[index+1] = prediction;
            if (train == 1) {
                rain_tr.put(parameterIndex.indexOf(headerLength), predT);
            } else {
                rain_ts.put(parameterIndex.indexOf(headerLength), predT);
            }
        } else {
            //do nothing
        }    
      
        if (parameterIndex.contains(headerLength + 1) || parameterIndex.contains(headerLength + 3)) {                             
            if (index == 0) {
                if (minT[index] < prediction) {
                    minT[index+1] = minT[index];
                } else {
                    minT[index+1] = prediction;
                }
                counterMin = 0;
            } else {
                if (predT[index-1] > predT[index] && prediction > predT[index]) {
                    minT[index+1] = predT[index];
                    counterMin = 0;
                } else {
                    minT[index+1] = minT[index - counterMin];
                    counterMin++;
                }
            }
            if (train == 1) {
                if (parameterIndex.contains(headerLength + 1)) {
                    rain_tr.put(parameterIndex.indexOf(headerLength + 1), minT);
                } else {
                    //do nothing
                } 

                if (parameterIndex.contains(headerLength + 3)) {                  
                    diffMinT[index+1] = minT[index+1] - meanOfGam;
                    rain_tr.put(parameterIndex.indexOf(headerLength + 3), diffMinT);
                } else {
                //do nothing
                }
            } else {
                if (parameterIndex.contains(headerLength + 1)) {
                    rain_ts.put(parameterIndex.indexOf(headerLength + 1), minT);
                } else {
                    //do nothing
                }
                if (parameterIndex.contains(headerLength + 3)) {                  
                    diffMinT[index+1] = minT[index+1] - meanOfGam;
                    rain_ts.put(parameterIndex.indexOf(headerLength + 3), diffMinT);
                }
            }             
        } else {
            //do nothing
        } 
        if (parameterIndex.contains(headerLength + 2) || parameterIndex.contains(headerLength + 4)) {          
            if (index == 0) {
                if (maxT[index] > prediction) {
                    maxT[index+1] = maxT[index];
                } else {
                    maxT[index+1] = prediction;
                }
                counterMax = 0;
            } else {
                if (predT[index-1] < predT[index] && prediction < predT[index]) {
                    maxT[index+1] = predT[index];
                    counterMax = 0;
                } else {
                    maxT[index+1] = maxT[index-counterMax];
                    counterMax ++;
                }
            }
            if (train == 1) {
                if (parameterIndex.contains(headerLength + 2)) {
                    rain_tr.put(parameterIndex.indexOf(headerLength + 2), maxT);
                } else {
                    //do nothing
                } 
                
                if (parameterIndex.contains(headerLength + 4)) {                  
                    diffMaxT[index+1] = maxT[index+1] - meanOfGam;
                    rain_tr.put(parameterIndex.indexOf(headerLength + 4), diffMaxT);
                } else {
                //do nothing
                }
            } else {
                if (parameterIndex.contains(headerLength + 2)) {
                    rain_ts.put(parameterIndex.indexOf(headerLength + 2), maxT);
                } else {
                    //do nothing
                }
                if (parameterIndex.contains(headerLength + 4)) {                  
                    diffMaxT[index+1] = maxT[index+1] - meanOfGam;
                    rain_ts.put(parameterIndex.indexOf(headerLength + 4), diffMaxT);
                }
            }
        }
        if (index >= Run.contractLength - 1) {
            if (parameterIndex.contains(headerLength + 5) || parameterIndex.contains(headerLength + 7)) {
                double mini = minT[index - Run.contractLength + 1];
                for (int i = 1 ; i < Run.contractLength ; i++) {
                    if (mini > minT[index - Run.contractLength + 1 + i]) {
                        mini = minT[index - Run.contractLength + 1 + i];
                    } else {
                        //do nothing
                    }
                }
                listGamMin[index + 1] = mini;

                if (train == 1) {
                    if (parameterIndex.contains(headerLength + 5)) {
                        rain_tr.put(parameterIndex.indexOf(headerLength + 5), listGamMin);
                    } else {
                        //do nothing                            
                    }
                    if (parameterIndex.contains(headerLength + 7)) {
                        listGamMinDiff[index+1] = listGamMin[index+1] - meanOfMin;
                        rain_tr.put(parameterIndex.indexOf(headerLength + 7), listGamMinDiff);
                    } else {
                        //do nothing
                    }
                } else {
                    if (parameterIndex.contains(headerLength + 5)) {
                        rain_ts.put(parameterIndex.indexOf(headerLength + 5), listGamMin);
                    } else {
                        //do nothing                            
                    }
                    if (parameterIndex.contains(headerLength + 7)) {
                        listGamMinDiff[index+1] = listGamMin[index+1] - meanOfMin;
                        rain_ts.put(parameterIndex.indexOf(headerLength + 7), listGamMinDiff);
                    } else {
                        //do nothing
                    }
                }
            } else {
                //do nothing
            }
            if (parameterIndex.contains(headerLength + 6) || parameterIndex.contains(headerLength + 8)) {
                double maxi = maxT[index - Run.contractLength + 1];
                for (int i = 1 ; i < Run.contractLength ; i++) {
                    if (maxi < maxT[index - Run.contractLength + 1 + i]) {
                        maxi = maxT[index - Run.contractLength + 1 + i];
                    } else {
                        //do nothing
                    } 
                }
                listGamMax[index + 1] = maxi;

                if (train == 1) {
                    if (parameterIndex.contains(headerLength + 6)) {
                        rain_tr.put(parameterIndex.indexOf(headerLength + 6), listGamMax);
                    } else {
                        //do nothing                            
                    }
                    if (parameterIndex.contains(headerLength + 8)) {
                            listGamMaxDiff[index+1] = listGamMax[index+1] - meanOfMax;
                        rain_tr.put(parameterIndex.indexOf(headerLength + 8), listGamMaxDiff);
                    } else {
                        //do nothing
                    }
                } else {
                    if (parameterIndex.contains(headerLength + 6)) {
                        rain_ts.put(parameterIndex.indexOf(headerLength + 6), listGamMax);
                    } else {
                        //do nothing                            
                    }
                    if (parameterIndex.contains(headerLength + 8)) {
                        listGamMaxDiff[index+1] = listGamMax[index+1] - meanOfMax;
                        rain_ts.put(parameterIndex.indexOf(headerLength + 8), listGamMaxDiff);
                    } else {
                        //do nothing
                    }
                }

            } else {
                //do nothing
            }
        }  
    }
    
//    private void createEnv() {
//        trainEnv = new Expr[rows1][cols-1];
//        testEnv = new Expr[rows2][cols-1];  
//        
//        for (int i = 0; i < rows1; i++) {
//            for (int j = 1; j < cols; j++) {                
//                temp = rain_tr.get(j);
//                env[j-1] = new Variable(temp[i]);
//            }
//        }
//        for (int i = 0; i < rows2; i++) {
//            for (int j = 1; j <cols; j++) {
//                temp = rain_ts.get(j);
//            }
//        }
//    }
  
  public double eval(Expr evolvedMethod) {
        
      //Generate initial values for an individual
    if (flag == 1) { //Therefore, gamma values are required
        Parameter.useLowerBound = 0;
        Parameter.usePredCL = 0;
        gammaCheck(1);
    }
    double SE = 0.0D;
    for (int i = 0; i < rows1; i++) {
        for (int j = 1; j < cols; j++) {
        	System.out.println(cols);
            temp = rain_tr.get(j);
            env[j-1] = new Variable(temp[i]);
        }
      Object o = evolvedMethod.eval(env);
      
      double prediction = ((Double)o).doubleValue();
      if (Parameter.useLowerBound == 1) {
          if (i > 1) {
              if (Parameter.usePredCL == 1) {                  
                  prediction = listGamMin[i-1]; 
              } else {
                  prediction = minT[i-1];
              }
          }
      } else if (prediction < 0) { 
          prediction = 0;
      } else {
          //do nothing
      }
      temp = rain_tr.get(0);
      SE += Math.pow(prediction - temp[i], 2.0D);
      if (flag == 1 && i < rows1 -1) {
        updateYpred(1,prediction,i);
      }
    }
    double MSE = Arithmetic.sqrt(SE / rows1);
//    Double.MAX_VALUE 
    return MSE;
  }
  
  public double test(Expr evolvedMethod)
  {
        if (flag ==1) {//Therefore, gamma values are required
            Parameter.useLowerBound = 0;
            Parameter.usePredCL = 0;
            gammaCheck(0);
        }
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
      if (Parameter.useLowerBound == 1) {
          if (i > 1) {
              if (Parameter.usePredCL == 1) {                  
                  prediction = listGamMin[i-1]; 
              } else {
                  prediction = minT[i-1];
              }
          }
      } else if (prediction < 0) { 
          prediction = 0;
      } else {
          //do nothing
      }
      predictions = predictions + prediction + "\n";
      SE += Math.pow(prediction - temp[i], 2.0D);
      
      if (flag == 1 && i < rows2 - 1) {
        updateYpred(0,prediction,i);
      } else {
          //do nothing
      }
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
