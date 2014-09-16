package files;

public class Misc
{
  public static double[] copy(double[] array)
  {
    double[] copy = new double[array.length];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = array[i];
    }
    return copy;
  }
  
  public static double[] copy(double[][] array, int column)
  {
    double[] copy = new double[array.length];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = array[i][column];
    }
    return copy;
  }
}
