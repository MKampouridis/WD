package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Run
  implements MethodSet
{
  public static GA alg = null;
  public static Evaluator eval;
  public static int nRuns = 50;
  public static String filename = "Luxembourg";
  public static int contractLength = 31;
  public static int maxInitialDepth = 2;
  public static int maxDepth = 6;
  public static int nGens = 50;
  public static int popSize = 500;
  public static int tournamentSize = 4;
  public static double mutProb = 0.01D;
  public static double xoverProb = 0.9D;
  public static double elitismPercentage = 0.01D;
  public static double terminals = 100.0D;
  public static double weights = 40.0D;
  public static int totalT = 2;
  public static int totalY = 10;
  public static int totalYears = 11;
  public static String filenameS;
  //private static Expr[] evolvedMethodParameters = new Expr[totalT+totalY];
  
  public static void main(String[] args)
  {
    while(totalT <= 11) { 
    while (totalYears <= 20) { //repeat for number of years in a file
    Expr[] evolvedMethodParameters = new Expr[totalT+totalY];
    eval = new PredictionEvaluatorTrue2(nRuns, filename, contractLength);
    
    Function evolvedMethod = new Function(Double.TYPE, new Class[0]);
    TreeManager.evolvedMethod = evolvedMethod;

        
    for (int i=0; i<totalT+totalY; i++) {
        
        evolvedMethodParameters[i] = new Parameter(i);
    }     
    TreeManager.evolvedMethodParameters = evolvedMethodParameters;
    
    ArrayList methodSet = new ArrayList();
    methodSet.add(ADD);
    methodSet.add(SUB);
    methodSet.add(MUL);
    methodSet.add(DIV);
    methodSet.add(LOG);
    methodSet.add(SQRT);
    //methodSet.add(POW);
    methodSet.add(MOD);
//    methodSet.add(SIN);
//    methodSet.add(COS);
    //methodSet.add(EXP);


    Random r = new Random();
    ArrayList terminalSet = new ArrayList();
//    for (int i = 0; i < terminals; i++)
//    {
//      double rc = r.nextDouble();
//      terminalSet.add(new Constant(new Double(rc * 100.0D), Double.TYPE)); //Building in a function representing random numbers minimum and maximum, consider avearge
//    }
//    
//    //Add in numbers between 0 and 2 in blocks of 0.05 for the purpose of weights
//    
//    for (int i = 0; i < weights; i++)
//    {
//      double rc = (1 + i) * 0.05;
//      terminalSet.add(new Constant(new Double(rc), Double.TYPE));
//    }
    
    //terminalSet.add(new Constant(new Double(0.0D), Double.TYPE));
    //terminalSet.add(new Constant(new Double(3.141592653589793D), Double.TYPE));
    
    //Dynamically adds the number of parameters to be estimated, need to refer to data to input correct values
    for (int i = 0; i < totalT; i++) {
        terminalSet.add(new Parameter(i, Double.TYPE, Boolean.valueOf(true), "Rain_t-"+(i+1)));
    }
    for (int i = 0; i < totalY; i++) {
        terminalSet.add(new Parameter(i+totalT, Double.TYPE, Boolean.valueOf(true), "Year_t-"+(i+1)));
    }
    
    
    
    double primProb = 0.6D;
    double terminalNodeCrossBias = 0.1D;
    TreeManager tm = new TreeManager(methodSet, terminalSet, primProb, maxInitialDepth, maxDepth, terminalNodeCrossBias);
    



    System.out.println("============= Experimental parameters =============");
    System.out.println("Maximum initial depth: " + maxInitialDepth);
    System.out.println("Maximum depth: " + maxDepth);
    System.out.println("Primitive probability in Grow method: " + primProb);
    System.out.println("Terminal node crossover bias: " + terminalNodeCrossBias);
    System.out.println("No of generations: " + nGens);
    System.out.println("Population size: " + popSize);
    System.out.println("Tournament size: " + tournamentSize);
    System.out.println("Crossover probability: " + xoverProb);
    System.out.println("Reproduction probability: " + (1.0D - xoverProb));
    System.out.println("Mutation probalitity: " + mutProb);
    System.out.println("Elitism percentage: " + elitismPercentage);
    System.out.println("===================================================");
    
    if (totalYears == 1)
    StatisticalSummary.logExperimentSetup(methodSet, terminalSet, maxInitialDepth, maxDepth, primProb, terminalNodeCrossBias, nGens, popSize, tournamentSize, mutProb, xoverProb);
    


    
    
        StatisticalSummary stat = null;
    filenameS = "Results_"+totalT+"_"+totalY+"_"+contractLength+"_"+totalYears;
    for (int i = 0; i < nRuns; i++)
    {
      System.out.println("========================== Experiment " + i + " ==================================");
      File experiment = new File(filenameS + "/Experiment "+i);
      experiment.mkdirs();
      stat = new StatisticalSummary(nGens, popSize, i);
      alg = new GA(tm, eval, popSize, tournamentSize, stat, mutProb, elitismPercentage, xoverProb, nRuns);
      alg.evolve(nGens, i);
      System.out.println("===============================================================================");
    }
    totalYears++;
    } System.out.println(totalT);
    totalT++;
    totalYears = 11;
  }
  }
}
