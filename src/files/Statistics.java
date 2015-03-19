package files;

import java.util.ArrayList;
import java.util.Random;

public class Statistics
{
  public static double sum(double[] array)
  {
    double sum = 0.0D;
    for (int i = 0; i < array.length; i++) {
      sum += array[i];
    }
    return sum;
  }
  
  public static double sum(ArrayList<Double> list)
  {
    double sum = 0.0D;
    for (int i = 0; i < list.size(); i++) {
      sum += ((Double)list.get(i)).doubleValue();
    }
    return sum;
  }
  
  public static double mean(double[] array)
  {
    return sum(array) / array.length;
  }
  
  public static double mean(ArrayList<Double> list)
  {
    double mean = 0.0D;
    for (int i = 0; i < list.size(); i++) {
      mean += ((Double)list.get(i)).doubleValue();
    }
    return mean / list.size();
  }
  
  public static double mean(Object[] arr)
  {
    double mean = 0.0D;
    for (int i = 0; i < arr.length; i++)
    {
      Double current = (Double)arr[i];
      mean += current.doubleValue();
    }
    return mean / arr.length;
  }
  
  public static double[] mean2(double[][] arr)
  {
    double[] mean = new double[arr[0].length];
    for (int col = 0; col < arr[0].length; col++)
    {
      for (int row = 0; row < arr.length; row++)
      {
        double cell = arr[row][col];
        mean[col] += cell;
      }
      mean[col] /= arr.length;
    }
    return mean;
  }
  
  public static double max(double a, double b)
  {
    if (a > b) {
      return a;
    }
    return b;
  }
  
  public static double[] max(double[][] arr)
  {
    double[] max = new double[arr[0].length];
    for (int col = 0; col < arr[0].length; col++)
    {
      double curMax = arr[0][col];
      for (int row = 1; row < arr.length; row++)
      {
        double cell = arr[row][col];
        if (cell > curMax) {
          curMax = cell;
        }
      }
      max[col] = curMax;
    }
    return max;
  }
  
  public static double max(ArrayList<Double> list)
  {
    double max = ((Double)list.get(0)).doubleValue();
    for (int i = 1; i < list.size(); i++) {
      if (((Double)list.get(i)).doubleValue() > max) {
        max = ((Double)list.get(i)).doubleValue();
      }
    }
    return max;
  }
  
  public static double max(double[] array)
  {
    double max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    }
    return max;
  }
  
  public static <T extends Comparable<T>> T max(T[] array)
  {
    T max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i].compareTo(max) > 0) {
        max = array[i];
      }
    }
    return max;
  }
  
  public static int maxAtPosition(Object[] array)
  {
    int position = 0;
    double max = ((Double)array[0]).doubleValue();
    for (int i = 1; i < array.length; i++) {
      if (((Double)array[i]).doubleValue() > max)
      {
        position = i;
        max = ((Double)array[i]).doubleValue();
      }
    }
    return position;
  }
  
  public static int maxAtPosition(ArrayList<Double> list)
  {
    int position = 0;
    double max = ((Double)list.get(0)).doubleValue();
    for (int i = 1; i < list.size(); i++) {
      if (((Double)list.get(i)).doubleValue() > max)
      {
        position = i;
        max = ((Double)list.get(i)).doubleValue();
      }
    }
    return position;
  }
  
  public static double min(double a, double b)
  {
    if (a < b) {
      return a;
    }
    return b;
  }
  
  public static double[] min(double[][] arr)
  {
    double[] min = new double[arr[0].length];
    for (int col = 0; col < arr[0].length; col++)
    {
      double curMin = arr[0][col];
      for (int row = 1; row < arr.length; row++)
      {
        double cell = arr[row][col];
        if (cell < curMin) {
          curMin = cell;
        }
      }
      min[col] = curMin;
    }
    return min;
  }
  
  public static <T extends Comparable<T>> T min(T[] array)
  {
    T min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i].compareTo(min) == 0) {
        min = array[i];
      }
    }
    return min;
  }
  
  public static double min(ArrayList<Double> list)
  {
    double min = ((Double)list.get(0)).doubleValue();
    for (int i = 1; i < list.size(); i++) {
      if (((Double)list.get(i)).doubleValue() < min) {
        min = ((Double)list.get(i)).doubleValue();
      }
    }
    return min;
  }
  
  public static double log(double number, double base)
  {
    return Math.log(number) / Math.log(base);
  }
  
  public static double stDev(ArrayList<Double> arr)
  {
    double stDev = 0.0D;
    for (int i = 0; i < arr.size(); i++) {
      stDev += Math.pow(((Double)arr.get(i)).doubleValue() - mean(arr), 2.0D);
    }
    stDev = Math.sqrt(stDev / (arr.size() - 1));
    
    return stDev;
  }
  
  public static double stDev(Object[] arr)
  {
    double stDev = 0.0D;
    for (int i = 0; i < arr.length; i++) {
      stDev += Math.pow(((Double)arr[i]).doubleValue() - mean(arr), 2.0D);
    }
    stDev = Math.sqrt(stDev / (arr.length - 1));
    
    return stDev;
  }
  
  public static double stDev(double[] arr)
  {
    double stDev = 0.0D;
    for (int i = 0; i < arr.length; i++) {
      stDev += Math.pow(arr[i] - mean(arr), 2.0D);
    }
    stDev = Math.sqrt(stDev / (arr.length - 1));
    
    return stDev;
  }
  
  public static double[] stDev(double[][] arr)
  {
    double[] stDev = new double[arr[0].length];
    double[] ar = new double[arr.length];
    for (int col = 0; col < arr[0].length; col++)
    {
      for (int row = 0; row < arr.length; row++) {
        ar[row] = arr[row][col];
      }
      for (int row = 0; row < arr.length; row++) {
        stDev[col] += Math.pow(arr[row][col] - mean(ar), 2.0D);
      }
      stDev[col] = Math.sqrt(stDev[col] / (ar.length - 1));
    }
    return stDev;
  }
  
  public static void shuffle(Object[] obj)
  {
    Random rgen = new Random();
    for (int i = 0; i < obj.length; i++)
    {
      int randomPosition = rgen.nextInt(obj.length);
      Object temp = obj[i];
      obj[i] = obj[randomPosition];
      obj[randomPosition] = temp;
    }
  }
  
  public static double[][] transpose(double[][] array)
  {
    double[][] transpose = new double[array[0].length][array.length];
    for (int rows = 0; rows < array.length; rows++) {
      for (int cols = 0; cols < array[0].length; cols++) {
        transpose[cols][rows] = array[rows][cols];
      }
    }
    return transpose;
  }
}
