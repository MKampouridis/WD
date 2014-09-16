package main;

import java.lang.reflect.Method;

public class MethodCall
  implements Callable
{
  Method method;
  Object object;
  
  public MethodCall(Method method)
  {
    this.method = method;
  }
  
  public Object call(Object[] args)
  {
    try
    {
      return this.method.invoke(null, args);
    }
    catch (Exception e) {}
    return null;
  }
  
  public String toString()
  {
    return this.method.getName();
  }
}
