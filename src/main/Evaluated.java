package main;

import java.io.Serializable;
import java.util.HashSet;

public class Evaluated
  implements Comparable<Object>, Serializable
{
  private static final long serialVersionUID = 1L;
  public Double fitness;
  Expr node;
  String geneticOperator;
  public static FWriter writeTree;
  
  public Evaluated(Expr node)
  {
    this.node = node;
  }
  
  String fileName = "";
  
  public void print(int cursor)
  {
    this.fileName = (Run.filenameS+"/Experiment " + cursor + "/Tree" + cursor + ".txt");
    writeTree = new FWriter(this.fileName, cursor);
    ((Funcall)((Function)this.node).func).impl.print(System.out, "  ", new HashSet());
    writeTree.closeFile();
  }
  
  public void print()
  {
    ((Funcall)((Function)this.node).func).impl.print2(System.out, "  ", new HashSet());
  }
  
  public void setGeneticOperator(String genOp)
  {
    this.geneticOperator = genOp;
  }
  
  public String getGeneticOperator()
  {
    return this.geneticOperator;
  }
  
  public void setFitness(double x)
  {
    this.fitness = new Double(x);
  }
  
  public void evaluate(Evaluator eval)
  {
    setFitness(eval.eval(this.node));
  }
  
  public void test(Evaluator eval)
  {
    setFitness(eval.test(this.node));
  }
  
  public int compareTo(Object x)
  {
    try
    {
      Evaluated ex = (Evaluated)x;
      return this.fitness.compareTo(ex.fitness);
    }
    catch (Exception e) {}
    return 0;
  }
}
