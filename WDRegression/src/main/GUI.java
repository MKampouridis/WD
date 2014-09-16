package main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI
  extends JFrame
  implements ActionListener, MethodSet
{
  private static final long serialVersionUID = 1L;
  public static GA alg = null;
  private JTextField fName;
  private JTextField runs;
  private JTextField maxInDepth;
  private JTextField maximumDepth;
  private JTextField gens;
  private JTextField pop;
  private JTextField tourn;
  private JTextField mut;
  private JTextField xover;
  private JTextField elitism;
  private JTextField term;
  private JButton ok;
  private JCheckBox check;
  private MethodCall[] function = { ADD, SUB, MUL, DIV, EXP, LOG, SQRT, POW, MOD, SIN, COS };
  private JCheckBox[] f = new JCheckBox[this.function.length];
  public static int nRuns;
  public static String filename;
  public static int maxInitialDepth;
  public static int maxDepth;
  public static int nGens;
  public static int popSize;
  public static int tournamentSize;
  public static double mutProb;
  public static double xoverProb;
  public static double elitismPercentage;
  public static double terminals;
  ArrayList<Object> methodSet = new ArrayList();
  public static ArrayList<Expr> terminalSet = new ArrayList();
  public static Evaluator eval;
  
  public GUI()
  {
    for (int i = 0; i < this.function.length; i++) {
      this.f[i] = new JCheckBox(this.function[i].toString().toUpperCase(), true);
    }
    this.f[7].setSelected(false);
    
    setTitle("GP Parameters");
    JPanel p3 = new JPanel();
    
    p3.setLayout(new GridLayout(13, 1));
    p3.add(new JLabel("Dataset"));
    p3.add(this.fName = new JTextField("Amsterdam", 15));
    p3.add(new JLabel("Number of Runs"));
    p3.add(this.runs = new JTextField("50", 3));
    p3.add(new JLabel("Population Size"));
    p3.add(this.pop = new JTextField("500", 5));
    p3.add(new JLabel("Number of Generations"));
    p3.add(this.gens = new JTextField("50", 4));
    p3.add(new JLabel("Maximum Initial Depth"));
    p3.add(this.maxInDepth = new JTextField("2", 2));
    p3.add(new JLabel("Maximum Depth"));
    p3.add(this.maximumDepth = new JTextField("4", 2));
    p3.add(new JLabel("Tournament Size"));
    p3.add(this.tourn = new JTextField("4", 2));
    p3.add(new JLabel("Crossover Probability"));
    p3.add(this.xover = new JTextField("0.9", 4));
    p3.add(new JLabel("Mutation Probability"));
    p3.add(this.mut = new JTextField("0.01", 4));
    p3.add(new JLabel("Elitism Probability"));
    p3.add(this.elitism = new JTextField("0.01", 4));
    p3.add(new JLabel("Number of Terminals"));
    p3.add(this.term = new JTextField("10", 2));
    




    JPanel p1 = new JPanel();
    p1.setLayout(new FlowLayout());
    p1.add(this.check = new JCheckBox("Show GDTs", true));
    for (int i = 0; i < 4; i++) {
      p1.add(this.f[i]);
    }
    JPanel p2 = new JPanel();
    p2.setLayout(new FlowLayout());
    for (int i = 4; i < this.function.length; i++) {
      p2.add(this.f[i]);
    }
    JPanel p5 = new JPanel();
    p5.setLayout(new FlowLayout());
    p5.add(this.ok = new JButton("OK"));
    this.ok.setMnemonic('\n');
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(p3, "North");
    
    getContentPane().add(p1, "Center");
    getContentPane().add(p2, "Last");
    getContentPane().add(p5, "After");
    
    this.ok.addActionListener(this);
  }
  
  public void run()
  {
    eval = new PredictionEvaluatorTrue2(nRuns, filename);
    
    Function evolvedMethod = new Function(Double.TYPE, new Class[0]);
    TreeManager.evolvedMethod = evolvedMethod;
    Expr[] evolvedMethodParameters = { new Parameter(0), new Parameter(1), new Parameter(2), new Parameter(3) };
    



    TreeManager.evolvedMethodParameters = evolvedMethodParameters;
    


    double primProb = 0.6D;
    double terminalNodeCrossBias = 0.1D;
    TreeManager tm = new TreeManager(this.methodSet, terminalSet, primProb, maxInitialDepth, maxDepth, terminalNodeCrossBias);
    



    System.out.println("============= Experimental parameters =============");
    System.out.println("Maximum initial depth: " + maxInitialDepth);
    System.out.println("Maximum depth: " + maxDepth);
    System.out.println("Primitive probability in Grow method: " + primProb);
    System.out.println("Terminal node crossover bias: " + terminalNodeCrossBias);
    System.out.println("No of generations: " + nGens);
    System.out.println("Population size: " + popSize);
    System.out.println("Tournament size: " + tournamentSize);
    System.out.println("Crossover probability: " + xoverProb);
    System.out.println("Reproduction probability: " + (1.0D - xoverProb));
    System.out.println("Mutation probalitity: " + mutProb);
    System.out.println("Elitism percentage: " + elitismPercentage);
    System.out.println("===================================================");
    StatisticalSummary.logExperimentSetup(this.methodSet, terminalSet, maxInitialDepth, maxDepth, primProb, terminalNodeCrossBias, nGens, popSize, tournamentSize, mutProb, xoverProb);
    


    StatisticalSummary stat = null;
    for (int i = 0; i < nRuns; i++)
    {
      System.out.println("========================== Experiment " + i + " ==================================");
      File experiment = new File("Results/Experiment " + i);
      experiment.mkdir();
      stat = new StatisticalSummary(nGens, popSize, i);
      alg = new GA(tm, eval, popSize, tournamentSize, stat, mutProb, elitismPercentage, xoverProb, nRuns);
      alg.evolve(nGens, i);
      System.out.println("===============================================================================");
    }
    GUIResults gui = new GUIResults();
    gui.setTitle("Summary Statistics");
    gui.setSize(1000, 550);
    gui.setVisible(true);
    System.out.println("End of experiments.");
  }
  
  public void actionPerformed(ActionEvent e)
  {
    if (this.check.isSelected()) {
      GA.showTree = true;
    }
    for (int i = 0; i < this.function.length; i++) {
      if (this.f[i].isSelected()) {
        this.methodSet.add(this.function[i]);
      }
    }
    if (e.getSource() == this.ok)
    {
      filename = this.fName.getText().trim();
      nRuns = Integer.parseInt(this.runs.getText().trim());
      maxInitialDepth = Integer.parseInt(this.maxInDepth.getText().trim());
      maxDepth = Integer.parseInt(this.maximumDepth.getText().trim());
      nGens = Integer.parseInt(this.gens.getText().trim());
      popSize = Integer.parseInt(this.pop.getText().trim());
      tournamentSize = Integer.parseInt(this.tourn.getText().trim());
      mutProb = Double.parseDouble(this.mut.getText().trim());
      xoverProb = Double.parseDouble(this.xover.getText().trim());
      elitismPercentage = Double.parseDouble(this.elitism.getText().trim());
      terminals = Double.parseDouble(this.term.getText().trim());
    }
    Random r = new Random();
    for (int i = 0; i < terminals; i++)
    {
      double rc = r.nextDouble();
      
      double rc2 = r.nextDouble();
      if (rc2 >= 0.5D) {
        rc = -rc;
      }
      terminalSet.add(new Constant(new Double(rc * 10.0D), Double.TYPE));
    }
    terminalSet.add(new Constant(new Double(3.141592653589793D), Double.TYPE));
    terminalSet.add(new Parameter(0, Double.TYPE, Boolean.valueOf(true), "Temp_t-1"));
    terminalSet.add(new Parameter(1, Double.TYPE, Boolean.valueOf(true), "Temp_t-2"));
    terminalSet.add(new Parameter(2, Double.TYPE, Boolean.valueOf(true), "Temp_t-3"));
    terminalSet.add(new Parameter(3, Double.TYPE, Boolean.valueOf(true), "t"));
    





    run();
  }
}
