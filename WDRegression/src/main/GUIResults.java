package main;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;

public class GUIResults
  extends JFrame
{
  private static final long serialVersionUID = 1L;
  private TextPanel descriptionPanel = new TextPanel();
  
  public GUIResults()
  {
    String description = PredictionEvaluatorTrue2.sumStat + PredictionEvaluatorTrue2.sumStat2;
    this.descriptionPanel.setDescription(description);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(this.descriptionPanel, "Center");
  }
}
