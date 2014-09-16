package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextPanel
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private JTextArea description;
  
  public TextPanel()
  {
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BorderLayout());
    

    JScrollPane scrollPane = new JScrollPane(this.description = new JTextArea());
    


    this.description.setFont(new Font("Serif", 0, 15));
    this.description.setWrapStyleWord(true);
    this.description.setEditable(false);
    
    scrollPane.setPreferredSize(new Dimension(250, 150));
    
    setLayout(new BorderLayout(15, 15));
    add(scrollPane, "Center");
    add(jPanel, "West");
  }
  
  public void setDescription(String text)
  {
    this.description.setText(text);
  }
}
