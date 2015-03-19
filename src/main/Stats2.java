package main;

import java.awt.Color;
import java.awt.Graphics;
import java.io.PrintStream;
import javax.swing.JPanel;

public class Stats2
  extends JPanel
{
  private int choose;
  static int diagr = 2;
  static int hist = 1;
  private static final long serialVersionUID = 1L;
  
  public Stats2()
  {
    draw(diagr);
  }
  
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    

    int width = getWidth();
    int height = getHeight();
    

    int x = 30;
    

    g.drawLine(30, height - 45, width - 50, height - 45);
    
    g.drawLine(30, height - 45, 30, height - 360);
    for (int i = 0; i <= 100; i += 10)
    {
      g.drawString(i + "", 0, height - x - 5);
      

      g.drawString("Eval. Test", (width - 150) / 4, height - 25);
      g.drawString("Test 1", (width - 80) / 2, height - 25);
      g.drawString("Test 2", 3 * (width - 80) / 4, height - 25);
      g.drawString("Test 3", width - 80, height - 25);
      x += 30;
    }
    System.out.println((int)(GA.stat.summaryStatistics[0][0] * 100.0D));
    if (this.choose == diagr)
    {
      g.setColor(Color.blue);
      g.drawLine((width - 60) / 4, getHeight() - 45 - (int)(GA.stat.summaryStatistics[0][0] * 100.0D), (width - 60) / 2, getHeight() - 45 - (int)(GA.stat.summaryStatistics[4][0] * 100.0D));
    }
    g.setColor(Color.blue);
    g.drawString(Integer.toString(45) + "%", (width - 80) / 4 + 5, height - 10);
    g.drawString(Integer.toString(67) + "%", (width - 80) / 2 + 5, height - 10);
    g.drawString(Integer.toString(89) + "%", 3 * (width - 80) / 4 + 5, height - 10);
    g.drawString(Integer.toString(99) + "%", width - 80 + 5, height - 10);
  }
  
  public void draw(int s)
  {
    this.choose = s;
    repaint();
  }
}
