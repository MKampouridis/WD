package main;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;

public class FWriter
{
  private File aFile;
  private FileWriter pw;
  
  public FWriter(String fileName, int cursor)
  {
    this.aFile = new File(fileName);
    if ((this.aFile.toString().equals("Results\\Accuracy.txt")) || (this.aFile.toString().equals("Results\\Summary Statistics.txt")) || (this.aFile.toString().equals("Results\\Experiment " + (cursor - 1) + "\\ConfusionMatrix.txt")))
    {
      if (cursor == 0)
      {
        this.aFile.delete();
        this.aFile = new File(fileName);
      }
    }
    else if (this.aFile.exists())
    {
      this.aFile.delete();
      this.aFile = new File(fileName);
    }
    try
    {
      this.pw = new FileWriter(this.aFile, true);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void writeLog(String record)
  {
    try
    {
      this.pw.write(record + "\n");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void writeLog2(String record)
  {
    try
    {
      this.pw.write(record + " ");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void openToAppend(File aFile)
  {
    try
    {
      this.pw = new FileWriter(aFile, true);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void closeFile()
  {
    try
    {
      this.pw.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.out.println("Error in closing the file");
    }
  }
}
