package main;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class Arithmetic
{
  static Method ADD;
  static Method SUB;
  static Method MUL;
  static Method DIV;
  static Method EXP;
  static Method LOG;
  static Method SQRT;
  static Method POW;
  static Method MOD;
  static Method SIN;
  static Method COS;
  
  static
  {
    try
    {
      Class<?>[] diadic = { Double.TYPE, Double.TYPE };
      Class<?>[] monadic = { Double.TYPE };
      ADD = Arithmetic.class.getDeclaredMethod("add", diadic);
      SUB = Arithmetic.class.getDeclaredMethod("sub", diadic);
      


      MUL = Arithmetic.class.getDeclaredMethod("mul", diadic);
      DIV = Arithmetic.class.getDeclaredMethod("div", diadic);
      EXP = Arithmetic.class.getDeclaredMethod("exp", monadic);
      LOG = Arithmetic.class.getDeclaredMethod("log", monadic);
      SQRT = Arithmetic.class.getDeclaredMethod("sqrt", monadic);
      POW = Arithmetic.class.getDeclaredMethod("power", diadic);
      MOD = Arithmetic.class.getDeclaredMethod("mod", diadic);
      SIN = Arithmetic.class.getDeclaredMethod("sin", monadic);
      COS = Arithmetic.class.getDeclaredMethod("cos", monadic);
    }
    catch (Exception e)
    {
      System.out.println("Arithmetic.static" + e);
    }
  }
  
  static Method[] methods = { MUL };
  static double eps = 1.E-005D;
  
  public static double add(double x, double y)
  {
    return x + y;
  }
  
  public static double sub(double x, double y)
  {
    return x - y;
  }
  
  public static double mul(double x, double y)
  {
    return x * y;
  }
  
  public static double div(double x, double y)
  {
    if (y < eps) {
      return 0.0D;
    }
    return x / y;
  }
  
  public static double mod(double x, double y)
  {
    if (y < eps) {
      return 0.0D;
    }
    return x % y;
  }
  
  public static double exp(double x)
  {
    return Math.exp(x);
  }
  
  public static double log(double x)
  {
    if (x < eps) {
      return 0.0D;
    }
    return Math.log(x);
  }
  
  public static double sqrt(double x)
  {
    if (x < 0.0D) {
      return Math.sqrt(0.0D);
    }
    return Math.sqrt(x);
  }
  
  public static double power(double x, double y)
  {
    return Math.pow(x, y);
  }
  
  public static double sin(double x)
  {
    return Math.sin(x);
  }
  
  public static double cos(double x)
  {
    return Math.cos(x);
  }
  
  public static void main(String[] args)
  {
    System.out.println(exp(0.0D));
    System.out.println(exp(-80.0D));
    System.out.println(log(0.0D));
    System.out.println(log(-1.0D));
    System.out.println(sqrt(-0.45D));
    System.out.println(sqrt(-10.0D));
    System.out.println(exp(0.0D));
    System.out.println(exp(-10.0D));
    System.out.println(power(-0.6988493070826418D, -0.6988493070826418D));
  }
}
