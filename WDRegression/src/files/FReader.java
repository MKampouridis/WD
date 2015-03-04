package files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FReader
{
  FileReader frs = null;
  String fileName = null;
  StreamTokenizer in = null;
  BufferedReader br = null;
  static String delimiter = "[\t\n]";
  static String splitter = ",";
  
  public static List<String[]> read(String inputFileName, String delimiter, String splitter)
    throws FileNotFoundException
  {
    File file = new File(inputFileName);
    Scanner input = new Scanner(file);
    input.useDelimiter(delimiter);
    String[] t1 = null;
    List<String[]> list = new ArrayList();
    while (input.hasNext())
    {
      t1 = input.next().split(splitter);
      list.add(t1);
    }
    input.close();
    return list;
  }
  
  public static List<String[]> read(String inputFileName)
    throws FileNotFoundException
  {
    File file = new File(inputFileName);
    Scanner input = new Scanner(file);
    input.useDelimiter(delimiter);
    String[] t1 = null;
    List<String[]> list = new ArrayList();
    while (input.hasNext())
    {
      t1 = input.next().split(splitter);
      list.add(t1);
    }
    input.close();
    return list;
  }
  
  public static int getColumnLength(String fileName)
    throws IOException
  {
    int length = 1;
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    
    int columnCounter = 1;
    
    String thisLine = br.readLine();
    for (int i = 0; i < thisLine.length(); i++)
    {
      String a = thisLine.substring(i, i + 1);
      if (a.equals(",")) {
        columnCounter++;
      }
    }
    length = columnCounter;
    
    return length;
  }
  
  public static int getRowLength(String fileName)
    throws IOException
  {
    int length = 0;
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    
    int rowCounter = 0;
    String thisLine;
    while ((thisLine = br.readLine()) != null) {
      rowCounter++;
    }
    length = rowCounter;
    
    return length;
  }
  
  public static void read(String fileName, double[] array)
    throws IOException
  {
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    

    int row = 0;
    String thisLine;
    while ((thisLine = br.readLine()) != null)
    {
      array[row] = Double.parseDouble(thisLine);
      row++;
    }
  }
  
  public static void read(String fileName, double[][] array)
    throws IOException
  {
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    if (array[0].length == 1)
    {
      int row = 0;
      String thisLine;
      while ((thisLine = br.readLine()) != null)
      {
        array[row][0] = Double.parseDouble(thisLine);
        row++;
      }
    }
    else
    {
      int row = 1;
      String thisLine;
      while ((thisLine = br.readLine()) != null)
      {
        ArrayList pos = new ArrayList();
        for (int i = 0; i < thisLine.length(); i++)
        {
          String a = thisLine.substring(i, i + 1);
          if (a.equals(",")) {
            pos.add(Integer.valueOf(i));
          }
        }
        int beginIndex = 1;
        int endIndex = ((Integer)pos.get(0)).intValue();
        array[row][0] = Double.parseDouble(thisLine.substring(beginIndex, endIndex));
        for (int i = 1; i < array[0].length; i++)
        {
          beginIndex = endIndex + 1;
          if (pos.size() > i) {
            endIndex = ((Integer)pos.get(i)).intValue();
          } else {
            endIndex = thisLine.length();
          }
          array[row][i] = Double.parseDouble(thisLine.substring(beginIndex, endIndex));
        }
        pos.clear();
        row++;
      }
    }
  }
  public void readArray(String filename, double[][] array, int col){
      try {
          BufferedReader reader = new BufferedReader(new FileReader(filename));
          int rowCounter = 0;
          String thisLine;
          reader.readLine();
          while ((thisLine = reader.readLine()) != null) {
              String[] cols = thisLine.split(",");
              for (int i = 0; i < col; i++) {              
              array[rowCounter][i] = Double.parseDouble(cols[i]);
              }
              rowCounter++;
          }
      }       
      catch (Exception e) { 
          e.printStackTrace();
      } 
  }
  
  public String[] readHeader(String filename) {
      try {
          BufferedReader reader = new BufferedReader(new FileReader(filename));          
          String thisLine = reader.readLine();
          String[] cols = thisLine.split(" ");
          return cols;
      }       
      catch (Exception e) { 
          e.printStackTrace();
      }
      return null;
  }
}
