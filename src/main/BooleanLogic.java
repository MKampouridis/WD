package main;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class BooleanLogic
{
  static Method AND;
  static Method OR;
  static Method NAND;
  static Method NOR;
  static Method NOT;
  
  static
  {
    try
    {
      Class<?>[] diadic = { Boolean.TYPE, Boolean.TYPE };
      AND = BooleanLogic.class.getDeclaredMethod("and", diadic);
      OR = BooleanLogic.class.getDeclaredMethod("or", diadic);
      NAND = BooleanLogic.class.getDeclaredMethod("nand", diadic);
      NOR = BooleanLogic.class.getDeclaredMethod("nor", diadic);
      NOT = BooleanLogic.class.getDeclaredMethod("not", new Class[] { Boolean.TYPE });
    }
    catch (Exception e)
    {
      System.out.println("BooleanLogic.static " + e);
    }
  }
  
  public static boolean and(boolean x, boolean y)
  {
    return (x) && (y);
  }
  
  public static boolean or(boolean x, boolean y)
  {
    return (x) || (y);
  }
  
  public static boolean nand(boolean x, boolean y)
  {
    return (!x) || (!y);
  }
  
  public static boolean nor(boolean x, boolean y)
  {
    return (!x) && (!y);
  }
  
  public static boolean not(boolean x)
  {
    return !x;
  }
}
