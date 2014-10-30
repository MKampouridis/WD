/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;

/**
 *
 * @author Sam
 */
public class MarkovChain {
    private final FileHelper fileHelper = new FileHelper();
    private final int width = fileHelper.checkWidth("C:\\WD\\luxembourgRainData.csv");//Will do the code to find absolute path later
    private final int length = fileHelper.checkLength("C:\\WD\\luxembourgRainData.csv");
    private BigDecimal a0, a1, b1, w, bda00, bda01, bda10, bda11, probRain, probDry, bdRain, bdDry, bdLength, condDR, condRR, condRD, condDD, bdDR, bdRR, transitionProb, uniformRnd;
    private final BigDecimal one = new BigDecimal(String.valueOf("1"));
    private final int days = 365;
    private final int years = length/days;
    private byte[] modelOccurence = new byte[days];
    private final int iterationNumber = 10000; //choose an iteration number, something above 30k using odd number in case model value is the same
    private int[][] rain = new int[length][5];
    private int[][][] occurenceNumDay = new int[days][2][2]; //3D array for each day and rain/dry following a rain/dry day
    private BigDecimal[][][] occurenceProbDay = new BigDecimal[days][2][2]; //3D array for the transition prob of each day
    private BigDecimal[][][] occurenceFSDay = new BigDecimal[days][2][2];
    private BigDecimal[][] bDfileReader = new BigDecimal[days][100];
    private String[] transitionProbReader = new String[days];
    
    /*
    Start the process off by checking whether a file has been preprocessed to start the Markov chain
    If it hasn't been then calculate the values first before calculating the Markov chain
    */        
    private void checkData() {   
        if (width == 2) {            //Therefore, no preprocessing as been done, currently only the rainfall amount and day 
            doProcessingDaily();    // Will calculate coniditional probabilities
            simulateMCRun();   // then run MC
            System.out.println(years);
        }
        
        else {
            simulateMCRun(); //conditional probabilties already exist, therefore, run simulations
        }
    }
    
    
    //AT SOME POINT LOOK HOW TO DO MLE TO PUT FOURIER SERIES IN HERE!!!
    
    
    /*
    Calculate the conditional probabilities of the rainfall amount for a P(W|D), P(D|W), P(W|W) and P(D|D) for each day of the year
    */
    private void doProcessingDaily() {
        //first step is to count the number of transitions from one state to another for each day
                
        fileHelper.readArray("C:\\WD\\luxembourgRainData.csv", rain,0); //create an array from the first column of the data only
        int countDry = 0;
        int countRain = 0;
        for (int i = 0; i < length; i++) {
            if (rain[i][0] == 0) {
                rain[i][1] = 0;
            } else {
                rain[i][1] = 1;
            }
        }
        
        System.out.println(rain.length);
        
        for (int i = 1; i <= days; i++) { //first element in data is only included to calculate transition number for the start of the new year
            int a00 = 0; //number of dry followed by dry
            int a01 = 0; //number of dry followed by wet
            int a10 = 0; //number of wet followed by dry
            int a11 = 0; //number of wet followed by wet
            for (int j = 0; j < years; j++) {
                if (rain[(j*days)+i-1][1] == 0 && rain[(j*days)+i][1] == 0) { //if day yesterday and today is dry
                    a00++; 
                } else if (rain[(j*days)+i-1][1] == 1 && rain[(j*days)+i][1] == 0) { //if day yesterday rains and today is dry
                    a10++;                    
                } else if (rain[(j*days)+i-1][1] == 1 && rain[(j*days)+i][1] == 1) {
                    a11++;
                } else {
                    a01++;
                }
            }
            //put the number of occurences into 2x2 matrix for each day]
            
            occurenceNumDay[i-1][0][0] = a00;
            occurenceNumDay[i-1][0][1] = a01;
            occurenceNumDay[i-1][1][0] = a10;
            occurenceNumDay[i-1][1][1] = a11;

            //calculate probabilties for each day
            bda00 = new BigDecimal(String.valueOf(a00));
            bda01 = new BigDecimal(String.valueOf(a01));
            bda10 = new BigDecimal(String.valueOf(a10));
            bda11 = new BigDecimal(String.valueOf(a11));
            
            occurenceProbDay[i-1][0][0] = bda00.divide((bda00.add(bda01)),4,RoundingMode.HALF_DOWN);
            occurenceProbDay[i-1][0][1] = one.subtract(occurenceProbDay[i-1][0][0]);
            occurenceProbDay[i-1][1][0] = bda10.divide((bda11.add(bda10)),4,RoundingMode.HALF_DOWN);
            occurenceProbDay[i-1][1][1] = one.subtract(occurenceProbDay[i-1][1][0]);
            
   
            fileHelper.readDArray("C:\\WD\\fsLux.csv",transitionProbReader,0);
            occurenceProbDay[i-1][0][0] = new BigDecimal(String.valueOf(transitionProbReader[i-1]));
            occurenceProbDay[i-1][0][1] = one.subtract(occurenceProbDay[i-1][0][0]);
            fileHelper.readDArray("C:\\WD\\fsLux.csv",transitionProbReader,1);
            occurenceProbDay[i-1][1][0] = new BigDecimal(String.valueOf(transitionProbReader[i-1]));
            occurenceProbDay[i-1][1][1] = one.subtract(occurenceProbDay[i-1][1][0]);
            
            //java does like cos/sin arsehole
//            //fourier series for each day
//            //starting with p00
//            //fourier series given by a0 + sum_n=1^h[an cos(npit/L)+bn(npit/L)]
//            //a0 = new BigDecimal(String.valueOf("0"));
//            //an = new BigDecimal(String.valueOf("0"));
//            //bn = new BigDecimal(String.valueOf("0"));
//            
//            //BigDecimal n182 = new BigDecimal(String.valueOf("182"));
//            //BigDecimal n365 = new BigDecimal(String.valueOf("365"));
//            //a0 = (n182.multiply(occurenceProbDay[i-1][0][0])).divide(n365);
//            //an = 
//            
//            //USING VALUES FROM MATLAB, WILL NEED TO BE INTEGRATED AT SOME POINT!!! PDD
//            //FOR LUX a0 = 0.7022, a1 = -0.0370, b1 = -0.0050 w = 0.0379
//            a0 = new BigDecimal(String.valueOf("0.7022"));
//            a1 = new BigDecimal(String.valueOf("-0.0370"));
//            b1 = new BigDecimal(String.valueOf("-0.0050"));
//            w = new BigDecimal(String.valueOf("0.0379"));
//            BigDecimal day = new BigDecimal(Integer.valueOf(i));
//            occurenceProbDay[i-1][0][0] = a0.add(a1.multiply(bdm.cos(day.multiply(w)))).add(b1.multiply(bdm.sin(day.multiply(w))));
//            occurenceProbDay[i-1][0][1] = one.subtract(occurenceProbDay[i-1][0][0]);
//            
//            //USING VALUES FROM MATLAB, WILL NEED TO BE INTEGRATED AT SOME POINT!!! PDD
//            //FOR LUX a0 = 0.3008, a1 = -0.0535, b1 = 0.01082 w = 0.0016
//            a0 = new BigDecimal(String.valueOf("0.3008"));
//            a1 = new BigDecimal(String.valueOf("-0.0535"));
//            b1 = new BigDecimal(String.valueOf("-0.01082"));
//            w = new BigDecimal(String.valueOf("0.0016"));
//            occurenceProbDay[i-1][1][0] = a0.add(a1.multiply(bdm.cos(day.multiply(w)))).add(b1.multiply(bdm.sin(day.multiply(w).setScale(2, RoundingMode.HALF_UP))));
//            occurenceProbDay[i-1][1][1] = one.subtract(occurenceProbDay[i-1][1][0]);
            
        }
        
        
        //For now type probs in manually
        
        
        
        
        System.out.println("PDD " + "PDR " + "PRD " + "PRR ");
        for (int i = 0; i < days; i++) {
            System.out.print(occurenceProbDay[i][0][0] + " " + occurenceProbDay[i][0][1] + " " + occurenceProbDay[i][1][0] + " " + occurenceProbDay[i][1][1]);
            System.out.println();
        }
        
        
        
//        
//        
//        bdLength = new BigDecimal(String.valueOf(length));
//        bdDry = new BigDecimal(String.valueOf(countDry));
//        bdRain = new BigDecimal(String.valueOf(countRain));
//        probRain = bdRain.divide(bdLength,5,RoundingMode.HALF_DOWN);
//        probDry = bdDry.divide(bdLength,5,RoundingMode.HALF_DOWN);
//        
//        int countCondRR = 0; //marked as 1
//        int countCondDR = 0; //marked as 2
//        for (int i = 1; i < length; i++) {
//            if (rain[i-1][1] == 1 && rain[i][1] == 1) {
//                rain[i][2] = 1;
//                countCondRR++;
//            } else if (rain[i-1][1] == 0 && rain[i][1] == 1) {
//                rain[i][2] = 2;
//                countCondDR++;
//            } 
//        }
//        bdDR = new BigDecimal(String.valueOf(countCondDR));
//        bdRR = new BigDecimal(String.valueOf(countCondRR));
//        BigDecimal a = new BigDecimal(String.valueOf("1"));
//        
//        condDR = bdDR.divide(bdDry,5,RoundingMode.HALF_DOWN);
//        condRR = bdRR.divide(bdRain,5,RoundingMode.HALF_DOWN);
//        condRD = a.subtract(condRR);
//        condDD = a.subtract(condDR);       
//        System.out.println("P(D|R) = " + condDR);
//        System.out.println("P(R|R) = " + condRR); //THESE ARE VERY GENERAL PROBABILITIES FIXED THROUGHOUT THE YEAR, next is to produce probabilities on a daily basis.
//        System.out.println("P(D|D) = " + condDD);
//        System.out.println("P(R|D) = " + condRD);
        
        
    }
    
    /*
    Simulate the Markov chain using Wilks(1998)
    */
    private void simulateMCRun() {
        if (width != 2) {
            //load the data in
        }
        byte[][] occurence = new byte[days][iterationNumber]; 
        for (int i = 0; i < iterationNumber; i++) { //main iteration loop
            occurence[0][i] = 0;
            for (int t = 1; t < days; t++) {//iterate over the year
                    //start dry day (model outcome) and create a random path from then on, from 2nd day onwards
                    Random r = new Random();
                    uniformRnd = newRandomBigDecimal(r, 4); //gives a uniformly distributed big decimal to compare transition probabilities against, change the accuracy to required number of dp
                    if (occurence[t-1][i] == 0) {
                        transitionProb = occurenceProbDay[t][0][1]; //in state 0                        
                        if (transitionProb.subtract(uniformRnd).doubleValue() >= 0) { //check whether we move to a rainy day, if positive 
                            occurence[t][i] = 1; 
                        } else { //else stay in the same state
                            occurence[t][i] = 0;
                        }
                    } else {
                        transitionProb = occurenceProbDay[t][1][0]; //in state 1
                        if (transitionProb.subtract(uniformRnd).doubleValue() >= 0) { //check whether we move to a dry day, if positive 
                            occurence[t][i] = 0;
                        } else {
                            occurence[t][i] = 1;
                        }
                    } 
                
            }
            //update probabilities
//            int countDry = 0;
//            int countRain = 0;
//            for (int k=0;k<length;k++) {
//                if (occurence[k][i] == 0) {
//                    countDry++;
//                } else {
//                    countRain++;
//                }
//            }
//            bdDry = new BigDecimal(String.valueOf(countDry));
//            bdRain = new BigDecimal(String.valueOf(countRain));
//            
//            int countCondRR = 0; //marked as 1
//            int countCondDR = 0; //marked as 2
//            for (int k = 1; k < length; k++) {
//            if (occurence[k-1][i] == 1 && occurence[k][i] == 1) {
//                countCondRR++;
//            } else if (occurence[k-1][i] == 0 && occurence[k][i] == 1) {
//                countCondDR++;
//            }
//            bdDR = new BigDecimal(String.valueOf(countCondDR));
//            bdRR = new BigDecimal(String.valueOf(countCondRR));
//            BigDecimal a = new BigDecimal(String.valueOf("1"));
//        
//            condDR = bdDR.divide(bdDry,5,RoundingMode.HALF_DOWN);
//            condRR = bdRR.divide(bdRain,5,RoundingMode.HALF_DOWN);
//            condRD = a.subtract(condRR);
//            condDD = a.subtract(condDR);
//            }
//            System.out.println("New Prob "+condDD);
        }
         
        //modelOccurence = checkModelOccurence(occurence);
//        for (int i = 0; i < days; i++) {
//        System.out.println(modelOccurence[i]);        
//        }
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < iterationNumber; j++) {
                System.out.print(occurence[i][j] + " ");       
            }
            System.out.println("");
        }
        
        //Calc
    }
    
    /*
    Method to checkData and begin MC run
    */
    public void runMarkovChain() {
        checkData();
    }
    
    private static BigDecimal newRandomBigDecimal(Random r, int precision) {
    BigInteger n = BigInteger.TEN.pow(precision);
    return new BigDecimal(newRandomBigInteger(n, r), precision);
    }

    private static BigInteger newRandomBigInteger(BigInteger n, Random rnd) {
        BigInteger r;        
        do { 
            r = new BigInteger(n.bitLength(), rnd);
        } while (r.compareTo(n) >= 0);
        return r;
    }
    
    private byte[] checkModelOccurence(byte[][] occur) {
        byte[] modelV = new byte[days];
        for (int i = 0; i < days; i++) {
            int counterD = 0;
            int counterR = 0;
            for (int j = 0; j < iterationNumber; j++) {
                byte num = occur[i][j]; 
                if (num == 0) {
                    counterD++;
                } else {
                    counterR++;
                }
            }            
            if (counterD > counterR) {
                modelV[i] = 0;                
            } else {
                modelV[i] = 1;
            }
        }
        return modelV;
    }
}
