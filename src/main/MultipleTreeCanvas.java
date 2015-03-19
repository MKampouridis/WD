package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class MultipleTreeCanvas
  extends Canvas
{
  private static final long serialVersionUID = 1L;
  private Tree[] trees;
  final int margin = 20;
  
  public void setTrees(Tree[] trees)
  {
    this.trees = trees;
    repaint();
  }
  
  public MultipleTreeCanvas(Tree[] t)
  {
    this.trees = t;
    setBackground(Color.white);
  }
  
  public void setTree(Tree t, int index)
  {
    this.trees[index] = t;
  }
  
  public void paint(Graphics g)
  {
    int w = 0;
    int maxH = 0;
    for (int i = 0; i < this.trees.length; i++)
    {
      Tree tree = this.trees[i];
      int thisHalfW = tree.getTreeWidth(g) / 2;
      int h = tree.getTreeHeight(g);
      if (h > maxH) {
        maxH = h;
      }
      w += thisHalfW;
      

      tree.drawTree(g, 0, 25);
      
      w += thisHalfW + 20;
    }
    setSize(w, maxH + 20);
  }
  
  public Dimension getSize(Graphics g)
  {
    int w = 0;
    int h = 0;
    for (int i = 0; i < this.trees.length; i++)
    {
      Tree tree = this.trees[i];
      int thisHalfW = tree.getTreeWidth(g) / 2;
      int thisH = tree.getTreeHeight(g);
      if (thisH > h) {
        h = thisH;
      }
      w += thisHalfW;
      w += thisHalfW + 20;
    }
    h += 40;
    
    return new Dimension(w + 15, h + 30);
  }
}
