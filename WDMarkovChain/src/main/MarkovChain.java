/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author Sam
 */
public class MarkovChain {
    private final FileHelper fileHelper = new FileHelper();
    private final int width = fileHelper.checkWidth("C:\\WD\\luxembourgRainData.csv");//Will do the code to find absolute path later
    private final int length = fileHelper.checkLength("C:\\WD\\luxembourgRainData.csv");
    BigDecimal probRain, probDry, bdRain, bdDry, bdLength, condDR, condRR, bdDR, bdRR;
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
        int countCondRD = 0; //marked as 3
        int countCondDD = 0; //marked as 4 
        for (int i = 1; i < length; i++) {
            if (rain[i-1][1] == 1 && rain[i][1] == 1) {
                rain[i][2] = 1;
                countCondRR++;
            } else if (rain[i-1][1] == 0 && rain[i][1] == 1) {
                rain[i][2] = 2;
                countCondDR++;
            } else if (rain[i-1][1] == 1 && rain[i][1] == 0) {
                rain[i][2] = 3;
                countCondRD++;
            } else if (rain[i-1][1] == 0 && rain[i][1] == 0) {
                rain[i][2] = 4;
                countCondDD++;
            } 
        }
        bdDR = new BigDecimal(String.valueOf(countCondDR));
        bdRR = new BigDecimal(String.valueOf(countCondRR));
        
        condDR = bdDR.divide(bdLength,5,RoundingMode.HALF_DOWN);
        condRR = bdRR.divide(bdLength,5,RoundingMode.HALF_DOWN);
        System.out.println("P(R|D) = " + condDR);
        System.out.println("P(R|R) = " + condRR); //THESE ARE VERY GENERAL PROBABILITIES FIXED THROUGHOUT THE YEAR, next is to produce probabilities on a daily basis.
    }
    
    /*
    Simulate the Markov chain using Wilks(1998)
    */
    private void simulateMCRun() {
        if (width != 2) {
            //load the data in
        }
        
    }
    
    /*
    Method to checkData and begin MC run
    */
    public void runMarkovChain() {
        checkData();
    }
    
}
