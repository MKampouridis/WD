package main;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Stats
  extends JFrame
  implements ActionListener
{
  private static final long serialVersionUID = 1L;
  private Stats2 stats2 = new Stats2();
  private JPanel buttonPanel = new JPanel();
  private JRadioButton histogram = new JRadioButton("Histogram");
  private JRadioButton diagram = new JRadioButton("Diagram", true);
  
  public Stats()
  {
    super("Stats");
    this.buttonPanel.setLayout(new GridLayout(2, 1, 0, 100));
    this.buttonPanel.add(this.histogram);
    this.buttonPanel.add(this.diagram);
    

    Container c = getContentPane();
    c.add(this.stats2, "Center");
    c.add(this.buttonPanel, "East");
    

    ButtonGroup group = new ButtonGroup();
    group.add(this.histogram);
    group.add(this.diagram);
    

    this.histogram.addActionListener(this);
    this.diagram.addActionListener(this);
  }
  
  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.histogram) {
      this.stats2.draw(Stats2.hist);
    } else if (e.getSource() == this.diagram) {
      this.stats2.draw(Stats2.diagr);
    }
  }
  
  public void run()
  {
    Stats frame = new Stats();
    
    frame.setTitle("Stats");
    frame.setSize(400, 450);
    frame.setVisible(true);
  }
}
