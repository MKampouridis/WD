package main;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

public class If
  implements Expr
{
  private static final long serialVersionUID = 1L;
  Expr condition;
  Expr trueAction;
  Expr falseAction;
  Expr condition1;
  Expr trueAction1;
  Expr condition2;
  Expr trueAction2;
  Expr otherwiseAction;
  Expr[] children;
  Class<?> returnType;
  Class<?>[] branchesTypes;
  FWriter fwriter;
  
  public If(Class<?> returnType, Class<?>[] branchesTypes)
  {
    this.returnType = returnType;
    this.branchesTypes = branchesTypes;
  }
  
  public If(Expr[] children, Class<?> returnType, Class<?>[] brachesTypes)
  {
    this.children = children;
    this.returnType = returnType;
    this.branchesTypes = brachesTypes;
  }
  
  public If(Expr[] children)
  {
    this.children = children;
  }
  
  public If(Expr[] children, Class<?> type)
  {
    this.children = children;
    this.returnType = type;
  }
  
  public void setBranches(Expr[] children)
  {
    this.children = children;
  }
  
  public Expr[] getChildren()
  {
    return this.children;
  }
  
  public Class<?> getType()
  {
    return this.returnType;
  }
  
  public String toString()
  {
    return new String("If-Then-Else");
  }
  
  public Expr copy(HashSet<Object> done, List<Object> evolvedMethod)
  {
    Expr[] kids = new Expr[this.children.length];
    for (int i = 0; i < kids.length; i++) {
      kids[i] = this.children[i].copy(done, evolvedMethod);
    }
    return new If(kids, this.returnType, this.branchesTypes);
  }
  
  public Object eval(Expr[] env)
  {
    try
    {
      Boolean result = (Boolean)this.children[0].eval(env);
      if (Boolean.TRUE.equals(result)) {
        return this.children[1].eval(env);
      }
      return this.children[2].eval(env);
    }
    catch (Exception e) {}
    return null;
  }
  
  public void print(PrintStream ps, String tab, HashSet<Object> done)
  {
    ps.println(tab + "(" + "If-Then-Else");
    Evaluated.writeTree.writeLog(tab + "(" + "If-Then-Else");
    if (!done.contains(this))
    {
      done.add(this);
      for (int i = 0; i < this.children.length; i++)
      {
        this.children[i].print(ps, tab + "   ", done);
        Evaluated.writeTree.writeLog(tab + "   ");
      }
      ps.println(tab + ")");
      Evaluated.writeTree.writeLog(tab + ")");
    }
  }
  
  public void bufferIt(StringBuffer buf, String tab, HashSet<Object> done)
  {
    buf.append(tab + "(" + "If-Then-Else" + "\n");
    if (!done.contains(this))
    {
      done.add(this);
      for (int i = 0; i < this.children.length; i++) {
        this.children[i].bufferIt(buf, tab + "   ", done);
      }
      buf.append(tab + ")" + "\n");
    }
  }
  
  public void setVal(Double val) {}
  
  public String getParameter()
  {
    return null;
  }
  
  public void print2(PrintStream ps, String tab, HashSet<Object> done)
  {
    ps.println(tab + "(" + "If-Then-Else");
    if (!done.contains(this))
    {
      done.add(this);
      for (int i = 0; i < this.children.length; i++) {
        this.children[i].print(ps, tab + "   ", done);
      }
      ps.println(tab + ")");
    }
  }
}
