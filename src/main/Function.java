package main;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

public class Function
  implements Expr
{
  private static final long serialVersionUID = 1L;
  Callable func;
  int arity;
  Class<?> returnType;
  Class<?>[] parameterTypes;
  Expr[] args;
  
  public Object eval(Expr[] env)
  {
    Object[] actual = new Object[this.args.length];
    for (int i = 0; i < this.args.length; i++) {
      actual[i] = this.args[i].eval(env);
    }
    return this.func.call(actual);
  }
  
  public Expr[] getChildren()
  {
    return this.args;
  }
  
  public Expr copy(HashSet<Object> done, List<Object> evolvedMethod)
  {
    Expr toReturn = null;
    
    Expr[] kids = new Expr[this.args.length];
    if ((this.func instanceof Funcall))
    {
      Funcall fc = (Funcall)this.func;
      if (done.contains(fc.impl))
      {
        toReturn = new Function(new Funcall((Expr)evolvedMethod.get(0)), kids, this.returnType, this.parameterTypes);
      }
      else
      {
        done.add(this);
        
        evolvedMethod.add(new Function(new Funcall(null), kids, this.returnType, this.parameterTypes));
        
        Expr copiedImpl = fc.impl.copy(done, evolvedMethod);
        ((Funcall)((Function)evolvedMethod.get(0)).func).impl = copiedImpl;
        toReturn = (Expr)evolvedMethod.get(0);
      }
    }
    else
    {
      toReturn = new Function(this.func, kids, this.returnType, this.parameterTypes);
    }
    for (int i = 0; i < kids.length; i++) {
      kids[i] = this.args[i].copy(done, evolvedMethod);
    }
    return toReturn;
  }
  
  public Class<?> getType()
  {
    if ((this.func == null) || ((this.func instanceof Funcall))) {
      return this.returnType;
    }
    MethodCall methCall = (MethodCall)this.func;
    return methCall.method.getReturnType();
  }
  
  public Function(Callable func, Expr[] args, Class<?> returnType, Class<?>[] parameterTypes)
  {
    this.func = func;
    this.args = args;
    this.returnType = returnType;
    this.parameterTypes = parameterTypes;
  }
  
  public Function(Class<?> returnType, Class<?>[] parameterTypes)
  {
    this.returnType = returnType;
    this.parameterTypes = parameterTypes;
  }
  
  public Function(Callable func, Expr[] args)
  {
    this.func = func;
    this.args = args;
  }
  
  public String toString()
  {
    if ((this.func instanceof MethodCall)) {
      return "Method:" + this.func;
    }
    return "Evolved_Method";
  }
  
  public void print(PrintStream ps, String tab, HashSet<Object> done)
  {
    ps.println(tab + "(" + this);
    Evaluated.writeTree.writeLog(tab + "(" + this);
    if (!done.contains(this))
    {
      done.add(this);
      for (int i = 0; i < this.args.length; i++)
      {
        this.args[i].print(ps, tab + "   ", done);
        Evaluated.writeTree.writeLog(tab + "   ");
      }
      ps.println(tab + ")");
      Evaluated.writeTree.writeLog(tab + ")");
    }
  }
  
  public void bufferIt(StringBuffer buf, String tab, HashSet<Object> done)
  {
    buf.append(tab + "(" + this + "\n");
    if (!done.contains(this))
    {
      done.add(this);
      for (int i = 0; i < this.args.length; i++) {
        this.args[i].bufferIt(buf, tab + "   ", done);
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
    ps.println(tab + "(" + this);
    if (!done.contains(this))
    {
      done.add(this);
      for (int i = 0; i < this.args.length; i++) {
        this.args[i].print2(ps, tab + "   ", done);
      }
      ps.println(tab + ")");
    }
  }
}
