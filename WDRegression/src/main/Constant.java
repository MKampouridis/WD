package main;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

public class Constant
  implements Expr
{
  private static final long serialVersionUID = 1L;
  Object val;
  public Class<?> type;
  Boolean param;
  Expr[] children = new Expr[0];
  
  public Constant(Object val, Class<?> type)
  {
    this.val = val;
    this.type = type;
  }
  
  public Expr[] getChildren()
  {
    return this.children;
  }
  
  public Class<?> getType()
  {
    return this.type;
  }
  
  public Object getVal()
  {
    return this.val;
  }
  
  public void setVal(Double val)
  {
    this.val = val;
  }
  
  public Boolean getParam()
  {
    return this.param;
  }
  
  public Expr copy(HashSet<Object> done, List<Object> evolvedMethod)
  {
    return new Constant(this.val, this.type);
  }
  
  public Object eval(Expr[] env)
  {
    return this.val;
  }
  
  public void print(PrintStream ps, String tab, HashSet<Object> done)
  {
    if (this.val.getClass().toString().equals("class java.lang.Short"))
    {
      ps.println(tab + "Constant : " + (((Short)this.val).shortValue() + 1));
      Evaluated.writeTree.writeLog(tab + "Constant : " + (((Short)this.val).shortValue() + 1));
    }
    else
    {
      ps.println(tab + "Constant : " + this.val);
      Evaluated.writeTree.writeLog(tab + "Constant : " + this.val);
    }
  }
  
  public void bufferIt(StringBuffer buf, String tab, HashSet<Object> done)
  {
    buf.append(tab + "Constant : " + this.val + "\n");
  }
  
  public String toString()
  {
    if (this.val != null)
    {
      if (this.val.getClass().toString().equals("class java.lang.Short"))
      {
        this.val = Integer.valueOf(((Short)this.val).shortValue() + 1);
        return this.val.toString();
      }
      return this.val.toString();
    }
    return null;
  }
  
  public String getParameter()
  {
    return null;
  }
  
  public void print2(PrintStream ps, String tab, HashSet<Object> done)
  {
    if (this.val.getClass().toString().equals("class java.lang.Short")) {
      ps.println(tab + "Constant : " + (((Short)this.val).shortValue() + 1));
    } else {
      ps.println(tab + "Constant : " + this.val);
    }
  }
}
