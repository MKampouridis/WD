package main;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

public abstract interface Expr
  extends Serializable
{
  public static final String indent = "   ";
  
  public abstract Object eval(Expr[] paramArrayOfExpr);
  
  public abstract Expr[] getChildren();
  
  public abstract Expr copy(HashSet<Object> paramHashSet, List<Object> paramList);
  
  public abstract Class<?> getType();
  
  public abstract void print(PrintStream paramPrintStream, String paramString, HashSet<Object> paramHashSet);
  
  public abstract void print2(PrintStream paramPrintStream, String paramString, HashSet<Object> paramHashSet);
  
  public abstract void bufferIt(StringBuffer paramStringBuffer, String paramString, HashSet<Object> paramHashSet);
  
  public abstract void setVal(Double paramDouble);
  
  public abstract String getParameter();
}
