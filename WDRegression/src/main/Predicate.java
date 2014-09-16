package main;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class Predicate
{
  static Method ET;
  static Method GT;
  static Method GTOE;
  static Method LT;
  static Method LTOE;
  
  static
  {
    try
    {
      Class<?>[] diadic = { Double.TYPE, Double.TYPE };
      ET = Predicate.class.getDeclaredMethod("et", diadic);
      GT = Predicate.class.getDeclaredMethod("gt", diadic);
      GTOE = Predicate.class.getDeclaredMethod("gtoe", diadic);
      LT = Predicate.class.getDeclaredMethod("lt", diadic);
      LTOE = Predicate.class.getDeclaredMethod("ltoe", diadic);
    }
    catch (Exception e)
    {
      System.out.println("Predicate.static: " + e);
    }
  }
  
  public static boolean et(double x, double compareTo)
  {
    return x == compareTo;
  }
  
  public static boolean gt(double x, double compareTo)
  {
    return x > compareTo;
  }
  
  public static boolean gtoe(double x, double compareTo)
  {
    return x >= compareTo;
  }
  
  public static boolean lt(double x, double compareTo)
  {
    return x < compareTo;
  }
  
  public static boolean ltoe(double x, double compareTo)
  {
    return x <= compareTo;
  }
}
