package main;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

public class Parameter
  implements Expr
{
    public static int useLowerBound = 0;
    public static int usePredCL = 0;
  private static final long serialVersionUID = 1L;
  int index;
  Class<?> type;
  Boolean param;
  Expr[] children = new Expr[0];
  String terminalFunction;
  
  public Parameter(int index)
  {
    this.index = index;
  }
  
  public Parameter(int index, Class<?> type, Boolean param)
  {
    this.index = index;
    this.type = type;
    this.param = param;
  }
  
  public Parameter(int index, Class<?> type, Boolean param, String terminalFunction)
  {
    this(index, type, param);
    this.terminalFunction = terminalFunction;
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
  
  public Expr copy(HashSet<Object> done, List<Object> evolvedMethod)
  {
    return new Parameter(this.index, this.type, this.param, this.terminalFunction);
  }
  
  public Object eval(Expr[] env)
  {
      if(terminalFunction != null) {
          if(terminalFunction.startsWith("PM") && Run.lowerBound == 1) {
              useLowerBound = 1;
              if(terminalFunction.endsWith("CL")) {
                  usePredCL = 1;
              } else {
                  //do nothing
              }
          } else {
          //do nothing
          }
      }
    return env[this.index].eval(null);
  }
  
  public String toString()
  {
    return "Parameter[" + this.index + "]";
  }
  
  public void print(PrintStream ps, String tab, HashSet<Object> done)
  {
    if (this.terminalFunction != null)
    {
      ps.println(tab + "Parameter[" + this.index + "]" + "[" + this.terminalFunction + "]");
      Evaluated.writeTree.writeLog(tab + "Parameter[" + this.index + "]" + "[" + this.terminalFunction + "]");
    }
    else
    {
      ps.println(tab + "Parameter[" + this.index + "]");
      Evaluated.writeTree.writeLog(tab + "Parameter[" + this.index + "]");
    }
  }
  
  public void print2(PrintStream ps, String tab, HashSet<Object> done)
  {
    if (this.terminalFunction != null) {
      ps.println(tab + "Parameter[" + this.index + "]" + "[" + this.terminalFunction + "]");
    } else {
      ps.println(tab + "Parameter[" + this.index + "]");
    }
  }
  
  public void bufferIt(StringBuffer buf, String tab, HashSet<Object> done)
  {
    buf.append(tab + "Parameter[" + this.index + "]" + "\n");
  }
  
  public void setVal(Double val) {}
  
  public String getParameter()
  {
    return null;
  }
}
