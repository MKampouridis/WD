package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHelper
{
  public ArrayList<ArrayList<Double>> read2DDArray(String filename)
  {
    try
    {
      ArrayList<ArrayList<Double>> array2D = new ArrayList();
      
      InputStream is = getClass().getResourceAsStream(filename);
      Scanner fileReader = new Scanner(is);
      while (fileReader.hasNext())
      {
        String input = fileReader.nextLine();
        String[] values = input.split("\t");
        ArrayList<Double> row = new ArrayList();
        for (int index = 0; index < values.length; index++) {
          row.add(Double.valueOf(Double.parseDouble(values[index])));
        }
        array2D.add(row);
      }
      return array2D;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public ArrayList<ArrayList<Integer>> read2DIArray(String filename)
  {
    try
    {
      ArrayList<ArrayList<Integer>> array2D = new ArrayList();
      
      InputStream is = getClass().getResourceAsStream(filename);
      Scanner fileReader = new Scanner(is);
      while (fileReader.hasNext())
      {
        String input = fileReader.nextLine();
        String[] values = input.split("\t");
        ArrayList<Integer> row = new ArrayList();
        for (int index = 0; index < values.length; index++) {
          row.add(Integer.valueOf(Integer.parseInt(values[index])));
        }
        array2D.add(row);
      }
      return array2D;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public ArrayList<Double> read1DDArray(String filename)
  {
    try
    {
      ArrayList<Double> array1D = new ArrayList();
      
      InputStream is = getClass().getResourceAsStream(filename);
      Scanner fileReader = new Scanner(is);
      while (fileReader.hasNext()) {
        array1D.add(Double.valueOf(fileReader.nextDouble()));
      }
      return array1D;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public ArrayList<Integer> read1DIArray(String filename)
  {
    try
    {
      ArrayList<Integer> array1D = new ArrayList();
      
      InputStream is = getClass().getResourceAsStream(filename);
      Scanner fileReader = new Scanner(is);
      while (fileReader.hasNext()) {
        array1D.add(Integer.valueOf(fileReader.nextInt()));
      }
      return array1D;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public void writeArray(ArrayList<String> list, String filename)
  {
    try
    {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
      for (int i = 0; i < list.size(); i++)
      {
        writer.write(((String)list.get(i)).toString());
        writer.newLine();
      }
      writer.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}