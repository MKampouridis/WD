package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Run
  implements MethodSet
{
  public static GA alg = null;
  public static Evaluator eval;
  public static int nRuns = 5;
  public static String filename = "Luxembourg";
  public static int maxInitialDepth = 2;
  public static int maxDepth = 4;
  public static int nGens = 25;
  public static int popSize = 250;
  public static int tournamentSize = 3;
  public static double mutProb = 0.01D;
  public static double xoverProb = 0.9D;
  public static double elitismPercentage = 0.01D;
  public static double terminals = 10.0D;
  
  public static void main(String[] args)
  {
    eval = new PredictionEvaluatorTrue2(nRuns, filename);
    
    Function evolvedMethod = new Function(Double.TYPE, new Class[0]);
    TreeManager.evolvedMethod = evolvedMethod;
    Expr[] evolvedMethodParameters = { new Parameter(0), new Parameter(1), new Parameter(2), new Parameter(3), new Parameter(4), new Parameter(5), new Parameter(6), new Parameter(7), new Parameter(8) };
    








    TreeManager.evolvedMethodParameters = evolvedMethodParameters;
    
    ArrayList methodSet = new ArrayList();
    methodSet.add(ADD);
    methodSet.add(SUB);
    methodSet.add(MUL);
    methodSet.add(DIV);
    methodSet.add(LOG);
    methodSet.add(SQRT);
    methodSet.add(POW);
    methodSet.add(MOD);
    methodSet.add(SIN);
    methodSet.add(COS);
    










    Random r = new Random();
    ArrayList terminalSet = new ArrayList();
    for (int i = 0; i < terminals; i++)
    {
      double rc = r.nextDouble();
      
      double rc2 = r.nextDouble();
      if (rc2 >= 0.5D) {
        rc = -rc;
      }
      terminalSet.add(new Constant(new Double(rc * 10.0D), Double.TYPE));
    }
    terminalSet.add(new Constant(new Double(0.0D), Double.TYPE));
    terminalSet.add(new Constant(new Double(3.141592653589793D), Double.TYPE));
    terminalSet.add(new Parameter(0, Double.TYPE, Boolean.valueOf(true), "Rain_t-1"));
    terminalSet.add(new Parameter(1, Double.TYPE, Boolean.valueOf(true), "Rain_t-2"));
    terminalSet.add(new Parameter(2, Double.TYPE, Boolean.valueOf(true), "Rain_t-3"));
    terminalSet.add(new Parameter(3, Double.TYPE, Boolean.valueOf(true), "Rain_t-4"));
    terminalSet.add(new Parameter(4, Double.TYPE, Boolean.valueOf(true), "Rain_t-5"));
    terminalSet.add(new Parameter(5, Double.TYPE, Boolean.valueOf(true), "Rain_t-6"));
    terminalSet.add(new Parameter(6, Double.TYPE, Boolean.valueOf(true), "Rain_t-7"));
    terminalSet.add(new Parameter(7, Double.TYPE, Boolean.valueOf(true), "Rain_t-8"));
    
    terminalSet.add(new Parameter(8, Double.TYPE, Boolean.valueOf(true), "t"));
    

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
    StatisticalSummary.logExperimentSetup(methodSet, terminalSet, maxInitialDepth, maxDepth, primProb, terminalNodeCrossBias, nGens, popSize, tournamentSize, mutProb, xoverProb);
    


    StatisticalSummary stat = null;
    for (int i = 0; i < nRuns; i++)
    {
      System.out.println("========================== Experiment " + i + " ==================================");
      File experiment = new File("Resultsj/Experiment " + i);
      experiment.mkdir();
      stat = new StatisticalSummary(nGens, popSize, i);
      alg = new GA(tm, eval, popSize, tournamentSize, stat, mutProb, elitismPercentage, xoverProb, nRuns);
      alg.evolve(nGens, i);
      System.out.println("===============================================================================");
    }
  }
}
