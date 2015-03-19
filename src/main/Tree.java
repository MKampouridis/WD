package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

public class Tree
{
  public String data;
  public Vector<Tree> children;
  private Font font = new Font("monospaced", 0, 10);
  private Color nodeColor = Color.lightGray;
  private Color leafColor = Color.green;
  private Color fontColor = Color.black;
  private Color edgeColor = Color.darkGray;
  
  public void setNodeColor(Color nodeColor)
  {
    this.nodeColor = nodeColor;
  }
  
  public void setLeafColor(Color leafColor)
  {
    this.leafColor = leafColor;
  }
  
  public void setFontColor(Color fontColor)
  {
    this.fontColor = fontColor;
  }
  
  public void setEdgeColor(Color edgeColor)
  {
    this.edgeColor = edgeColor;
  }
  
  public Tree(String s)
  {
    this.data = s;
    this.children = new Vector();
  }
  
  public void addChild(Tree t)
  {
    if (t != null) {
      this.children.addElement(t);
    }
  }
  
  public void removeChild(Tree t)
  {
    this.children.removeElement(t);
  }
  
  public boolean isLeaf()
  {
    return this.children.size() == 0;
  }
  
  public String toString()
  {
    return toString(0);
  }
  
  public String toString(int tab)
  {
    String result = "";
    for (int i = 0; i <= tab - 1; i++) {
      result = result + "  ";
    }
    result = result + "|-" + this.data + "\n";
    for (int j = 0; j <= this.children.size() - 1; j++) {
      result = result + ((Tree)this.children.elementAt(j)).toString(tab + 1);
    }
    return result;
  }
  
  public String toTreeExpression()
  {
    return null;
  }
  
  public int getNodeWidth(Graphics g)
  {
    g.setFont(this.font);
    FontMetrics fm = g.getFontMetrics(this.font);
    int w = fm.stringWidth(this.data);
    int margin = fm.stringWidth(" ");
    return 2 * margin + w;
  }
  
  public int getNodeHeight(Graphics g)
  {
    g.setFont(this.font);
    FontMetrics fm = g.getFontMetrics(this.font);
    int h = fm.getHeight();
    return 2 * h;
  }
  
  public void drawNode(Graphics g, int x, int y)
  {
    g.setFont(this.font);
    FontMetrics fm = g.getFontMetrics(this.font);
    if (isLeaf()) {
      g.setColor(this.leafColor);
    } else {
      g.setColor(this.nodeColor);
    }
    int width = fm.stringWidth(this.data);
    int margin = fm.stringWidth(" ");
    int height = fm.getHeight();
    g.fill3DRect(x, y, 2 * margin + width, 2 * height, true);
    g.setColor(this.fontColor);
    g.drawString(this.data, x + margin, y + (int)(1.3D * height));
  }
  
  public int getTreeWidth(Graphics g)
  {
    int w = getNodeWidth(g) + 10;
    int wc = 0;
    if (this.children.size() == 0) {
      return w;
    }
    for (int i = 0; i < this.children.size(); i++) {
      wc += ((Tree)this.children.elementAt(i)).getTreeWidth(g);
    }
    return Math.max(w, wc);
  }
  
  public int getTreeHeight(Graphics g)
  {
    int h = 2 * getNodeHeight(g);
    if (this.children.size() == 0) {
      return h;
    }
    int h1 = ((Tree)this.children.elementAt(0)).getTreeHeight(g);
    for (int i = 1; i < this.children.size(); i++)
    {
      int h2 = ((Tree)this.children.elementAt(i)).getTreeHeight(g);
      if (h2 > h1) {
        h1 = h2;
      }
    }
    return h + h1;
  }
  
  public void drawTree(Graphics g, int x, int y)
  {
    int nw = getNodeWidth(g);
    int tw = getTreeWidth(g);
    int h = getNodeHeight(g);
    drawNode(g, x + (tw - nw) / 2, y);
    if (this.children.size() > 0)
    {
      int dx = x;
      for (int k = 0; k < this.children.size(); k++)
      {
        Tree t = (Tree)this.children.elementAt(k);
        int cw = t.getTreeWidth(g);
        
        g.setColor(this.edgeColor);
        g.drawLine(x + tw / 2, y + h, dx + cw / 2, y + 2 * h);
        
        t.drawTree(g, dx, y + 2 * h);
        
        dx += cw;
      }
    }
  }
}
