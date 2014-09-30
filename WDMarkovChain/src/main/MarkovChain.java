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
    private BigDecimal probRain, probDry, bdRain, bdDry, bdLength, condDR, condRR, condRD, condDD, bdDR, bdRR, transitionProb, uniformRnd, zero;
    private final int days = 365;
    private byte[] modelOccurence = new byte[days];
    private final int iterationNumber = 3; //choose an iteration number, something above 30k using odd number in case model value is the same
    
    /*
    Start the process off by checking whether a file has been preprocessed to start the Markov chain
    If it hasn't been then calculate the values first before calculating the Markov chain
    */        
    private void checkData() {   
        if (width == 2) {            //Therefore, no preprocessing as been done, currently only the rainfall amount and day 
            doProcessing();    // Will calculate coniditional probabilities
            simulateMCRun();   // then run MC
            
        }
        
        else {
            simulateMCRun(); //conditional probabilties already exist, therefore, run simulations
        }
    }
    
    /*
    Calculate the conditional probabilities of the rainfall amount for a P(W|D), P(D|W), P(W|W) and P(D|D) for each day of the year
    */
    private void doProcessing() {
        int[][] rain = new int[length][5];
        fileHelper.readArray("C:\\WD\\luxembourgRainData.csv", rain); //create an array from the first column of the data only
        int countDry = 0;
        int countRain = 0;
        for (int i=0;i<length;i++) {
            if (rain[i][0] == 0) {
                rain[i][1] = 0;
                countDry++;
            } else {
                rain[i][1] = 1;
                countRain++;
            }
        }
        
        bdLength = new BigDecimal(String.valueOf(length));
        bdDry = new BigDecimal(String.valueOf(countDry));
        bdRain = new BigDecimal(String.valueOf(countRain));
        probRain = bdRain.divide(bdLength,5,RoundingMode.HALF_DOWN);
        probDry = bdDry.divide(bdLength,5,RoundingMode.HALF_DOWN);
        
        int countCondRR = 0; //marked as 1
        int countCondDR = 0; //marked as 2
        for (int i = 1; i < length; i++) {
            if (rain[i-1][1] == 1 && rain[i][1] == 1) {
                rain[i][2] = 1;
                countCondRR++;
            } else if (rain[i-1][1] == 0 && rain[i][1] == 1) {
                rain[i][2] = 2;
                countCondDR++;
            } 
        }
        bdDR = new BigDecimal(String.valueOf(countCondDR));
        bdRR = new BigDecimal(String.valueOf(countCondRR));
        BigDecimal a = new BigDecimal(String.valueOf("1"));
        
        condDR = bdDR.divide(bdLength,5,RoundingMode.HALF_DOWN);
        condRR = bdRR.divide(bdLength,5,RoundingMode.HALF_DOWN);
        condRD = a.subtract(condRR);
        condDD = a.subtract(condDR);       
        System.out.println("P(R|D) = " + condDR);
        System.out.println("P(R|R) = " + condRR); //THESE ARE VERY GENERAL PROBABILITIES FIXED THROUGHOUT THE YEAR, next is to produce probabilities on a daily basis.
        System.out.println("P(D|D) = " + condDD);
        System.out.println("P(D|R) = " + condRD);
    }
    
    /*
    Simulate the Markov chain using Wilks(1998)
    */
    private void simulateMCRun() {
        if (width != 2) {
            //load the data in
        }
        
        zero = new BigDecimal(String.valueOf("0"));
        byte[][] occurence = new byte[days][iterationNumber]; 
        for (int i = 0; i < iterationNumber; i++) { //main iteration loop
            occurence[0][i] = 0;
            for (int t = 1; t < days; t++) {//iterate over the year
                //start dry day (model outcome) and create a random path from then on, from 2nd day onwards
                Random r = new Random();
                uniformRnd = newRandomBigDecimal(r, 4); //gives a uniformly distributed big decimal to compare transition probabilities against, change the accuracy to required number of dp
                if (occurence[t-1][i] == 0) {
                    transitionProb = condDR; //in state 0
                    if (transitionProb.subtract(uniformRnd).doubleValue() >= 0) { //check whether we move to a rainy day, if positive 
                        occurence[t][i] = 1; 
                    } else { //else stay in the same state
                        occurence[t][i] = 0;
                    }
                } else {
                    transitionProb = condRR; //in state 1
                    if (transitionProb.subtract(uniformRnd).doubleValue() >= 0) { //check whether we move to a dry day, if positive 
                        occurence[t][i] = 0;
                    } else {
                        occurence[t][i] = 1;
                    }
                } 
            }
        }
         
        modelOccurence = checkModelOccurence(occurence);
        for (int i = 0; i < days; i++) {
        System.out.println(modelOccurence[i]);        
        }
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < iterationNumber; j++) {
                System.out.print(occurence[i][j]);       
            }
            System.out.println("");
        }
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
        int counter = 0;
        for (int i = 0; i < days; i++) {
            for (int j = 0; j < iterationNumber; j++) {
                
                if (occur[i][j] == 0) {
                    counter++;                        
                }
            }
            if (counter <= iterationNumber/2) {
                modelOccurence[i] = 1;
            } else {
                modelOccurence[i] = 0;                
            }
        }
        return modelOccurence;
    }
    
}
