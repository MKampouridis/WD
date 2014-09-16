package main;

public abstract interface Evaluator
{
  public abstract double eval(Expr paramExpr);
  
  public abstract double test(Expr paramExpr);
}
