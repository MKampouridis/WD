package main;

import java.io.Serializable;

public class Funcall
  implements Callable, Serializable
{
  private static final long serialVersionUID = 1L;
  Expr impl;
  
  public Funcall(Expr impl)
  {
    this.impl = impl;
  }
  
  public Object call(Object[] args)
  {
    Expr[] env = new Expr[args.length];
    for (int i = 0; i < args.length; i++) {
      env[i] = new Variable(args[i]);
    }
    return this.impl.eval(env);
  }
  
  public String toString()
  {
    return "Funcall: " + this.impl;
  }
}
