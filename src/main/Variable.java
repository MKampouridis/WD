package main;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

public class Variable
  implements Expr
{
  private static final long serialVersionUID = 1L;
  Object val;
  Expr[] children = new Expr[0];
  
  public Variable(Object val)
  {
    this.val = val;
  }
  
  public Expr[] getChildren()
  {
    return this.children;
  }
  
  public Class<?> getType()
  {
    return null;
  }
  
  public Object eval(Expr[] env)
  {
    return this.val;
  }
  
  public String toString()
  {
    return "var: " + this.val;
  }
  
  public Expr copy(HashSet<Object> done, List<Object> evolvedMethod)
  {
    return null;
  }
  
  public void print(PrintStream ps, String tab, HashSet<Object> done) {}
  
  public void bufferIt(StringBuffer buf, String tab, HashSet<Object> done) {}
  
  public void setVal(Double val)
  {
    this.val = val;
  }
  
  public String getParameter()
  {
    return null;
  }
  
  public void print2(PrintStream ps, String tab, HashSet<Object> done) {}
}
