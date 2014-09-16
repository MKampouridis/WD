package main;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

public class ADF
  implements Expr
{
  private static final long serialVersionUID = -5735445082127863703L;
  int index;
  Class<?> type;
  Boolean param;
  Expr[] children = new Expr[0];
  String adf;
  public String parameter;
  
  public ADF(int index)
  {
    this.index = index;
  }
  
  public ADF(int index, Class<?> type, Boolean param)
  {
    this.index = index;
    this.type = type;
    this.param = param;
  }
  
  public ADF(int index, Class<?> type, Boolean param, String adf, String parameter)
  {
    this(index, type, param);
    this.adf = adf;
    this.parameter = parameter;
  }
  
  public Expr[] getChildren()
  {
    return this.children;
  }
  
  public Class<?> getType()
  {
    return this.type;
  }
  
  public Boolean getParam()
  {
    return this.param;
  }
  
  public String getParameter()
  {
    return this.parameter;
  }
  
  public Expr copy(HashSet<Object> done, List<Object> evolvedMethod)
  {
    return new ADF(this.index, this.type, this.param, this.adf, this.parameter);
  }
  
  public Object eval(Expr[] env)
  {
    return env[this.index].eval(null);
  }
  
  public String toString()
  {
    return this.adf;
  }
  
  public void print(PrintStream ps, String tab, HashSet<Object> done)
  {
    if (this.adf != null)
    {
      ps.println(tab + "ADF[" + this.index + "]" + "[" + this.adf + "]");
      Evaluated.writeTree.writeLog(tab + "ADF[" + this.index + "]" + "[" + this.adf + "]");
    }
    else
    {
      ps.println(tab + "Parameter[" + this.index + "]");
      Evaluated.writeTree.writeLog(tab + "Parameter[" + this.index + "]");
    }
  }
  
  public void bufferIt(StringBuffer buf, String tab, HashSet<Object> done)
  {
    buf.append(tab + "ADF[" + this.index + "]" + "\n");
  }
  
  public void setVal(Double val) {}
  
  public void print2(PrintStream ps, String tab, HashSet<Object> done)
  {
    if (this.adf != null) {
      ps.println(tab + "ADF[" + this.index + "]" + "[" + this.adf + "]");
    } else {
      ps.println(tab + "Parameter[" + this.index + "]");
    }
  }
}
