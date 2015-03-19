package main;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class GA
{
  static int counter = 0;
  Evaluated[] pop;
  Evaluated[] intermediatePop;
  Evaluated[] tournamentParticipants;
  TreeManager tm;
  Evaluator eval;
  int popSize;
  int tournamentSize;
  double mutProb;
  double elitismPercentage;
  double crossoverProbability;
  static StatisticalSummary stat;
  static Random r = new Random();
  public static boolean showTree = false;
  int nRuns;
  
  public GA(TreeManager tm, Evaluator eval, int popSize, int tournamentSize, StatisticalSummary stat, double mutProb, double elitismPercentage, double crossoverProbability, int nRuns)
  {
    this.tm = tm;
    this.eval = eval;
    this.popSize = popSize;
    this.tournamentSize = tournamentSize;
    this.stat = stat;
    this.mutProb = mutProb;
    this.elitismPercentage = elitismPercentage;
    this.crossoverProbability = crossoverProbability;
    this.nRuns = nRuns;
    init();
  }
  
  public void init()
  {
    this.pop = new Evaluated[this.popSize];
    this.intermediatePop = new Evaluated[this.popSize];
    this.tournamentParticipants = new Evaluated[this.tournamentSize];
    Evaluated[] init = this.tm.initialPopulationRampedHalf(this.eval, this.popSize);
    for (int i = 0; i < this.popSize; i++) {
      this.pop[i] = init[i];
    }
  }
  
  public void evolve(int nGens, int cursor)
  {
    int i = 0;
    while (i < nGens)
    {
      System.out.println("GENERATION : " + i);
      evaluate();
      
      stat.addStat(this.pop, i);
      
      Arrays.sort(this.pop);
      if (i < nGens - 1)
      {
        breed(i);
        updatePop();
      }
      i++;
    }
    stat.logStatisticalSummary(nGens - 1);
    
    validate(cursor);
  }
  
  public void breed(int currentGeneration)
  {
    int elitismFlag = (int)(this.elitismPercentage * this.popSize);
    for (int i = 0; i < this.popSize; i++) {
      if (elitismFlag > 0)
      {
        this.intermediatePop[i] = new Evaluated(this.pop[i].node);
        elitismFlag--;
      }
      else
      {
        if (r.nextDouble() <= this.crossoverProbability)
        {
          Expr parent1 = select();
          Expr parent2 = select();
          this.intermediatePop[i] = new Evaluated(this.tm.crossover(parent1, parent2));
          this.intermediatePop[i].setGeneticOperator("Xover");
        }
        else
        {
          int randomIndex = r.nextInt(this.pop.length);
          this.intermediatePop[i] = new Evaluated(this.pop[randomIndex].node.copy(new HashSet(), new ArrayList()));
          this.intermediatePop[i].setGeneticOperator("Reproduction");
        }
        if (r.nextDouble() < this.mutProb)
        {
          Expr parent = this.intermediatePop[i].node;
          this.intermediatePop[i] = new Evaluated(this.tm.point_mutate(parent));
          
          this.intermediatePop[i].setGeneticOperator("Mutated");
        }
      }
    }
  }
  
  public void evaluate()
  {
    for (int i = 0; i < this.popSize; i++) {
      this.pop[i].evaluate(this.eval);
    }
  }
  
  public void validate(int cursor)
  {
    System.out.println("=== BEST PREDICTOR PROGRAM ===");
    this.pop[0].print(cursor);
    this.pop[0].evaluate(this.eval);
    System.out.println("Best individual's training fitness (RMSE): " + this.pop[0].fitness);
    this.pop[0].test(this.eval);
    System.out.println("Best individual's testing fitness (RMSE): " + this.pop[0].fitness);
    Expr node = this.pop[0].node.copy(new HashSet(), new ArrayList());
    Function f = (Function)node;
    Expr impl = ((Funcall)f.func).impl;
    if (showTree) {
      visualiseEvolved(impl, cursor, this.pop[0].fitness.doubleValue());
    }
    counter += 1;
  }
  
  public void updatePop()
  {
    for (int i = 0; i < this.popSize; i++) {
      this.pop[i] = this.intermediatePop[i];
    }
  }
  
  public void printPop()
  {
    System.out.println("Printing pop...................");
    for (int i = 0; i < this.pop.length; i++)
    {
      this.pop[i].print();
      System.out.println("MSE: " + this.pop[i].fitness);
      System.out.println("==========================================");
    }
  }
  
  public Expr select()
  {
    for (int i = 0; i < this.tournamentSize; i++)
    {
      int randomIndex = r.nextInt(this.pop.length);
      Evaluated eval = this.pop[randomIndex];
      this.tournamentParticipants[i] = eval;
    }
    Arrays.sort(this.tournamentParticipants);
    return this.tournamentParticipants[0].node;
  }
  
  public static void visualiseEvolved(Expr evolved, int cursor, double fitness)
  {
    Tree root = parseToVisualise(evolved);
    
    TreeScrollFrame frame = new TreeScrollFrame(new Tree[] { root }, "Run " + cursor + ", MSE: " + fitness);
  }
  
  public static Tree parseToVisualise(Expr node)
  {
    Tree tree = new Tree(node.toString());
    Expr[] kids = node.getChildren();
    for (int i = 0; i < kids.length; i++)
    {
      Tree kid = parseToVisualise(kids[i]);
      tree.addChild(kid);
    }
    return tree;
  }
}
