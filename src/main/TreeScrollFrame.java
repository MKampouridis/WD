package main;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.ScrollPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TreeScrollFrame
  extends Frame
{
  private static final long serialVersionUID = 1L;
  private Tree[] trees;
  private MultipleTreeCanvas tc = null;
  
  public TreeScrollFrame() {}
  
  public void setTrees(Tree[] trees)
  {
    this.trees = trees;
    this.tc.setTrees(trees);
  }
  
  public TreeScrollFrame(Tree[] t, String title)
  {
    super(title);
    this.trees = t;
    this.tc = new MultipleTreeCanvas(this.trees);
    
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent w)
      {
        TreeScrollFrame.this.setVisible(false);
      }
    });
    ScrollPane scrollpane = new ScrollPane(1);
    scrollpane.add(this.tc);
    add(scrollpane);
    

    show();
    setVisible(false);
    
    Graphics g = this.tc.getGraphics();
    setSize(this.tc.getSize(g));
    

    setVisible(true);
    pack();
  }
}
