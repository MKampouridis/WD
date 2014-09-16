package main;

import java.util.ArrayList;
import java.util.Random;

public class Util
  implements Cloneable
{
  static int depthBetweenNodes = 0;
  static int currDepth;
  
  public static void get_depth2(Expr cur, Expr target)
  {
    if (cur == target)
    {
      depthBetweenNodes = currDepth;
      return;
    }
    if (cur.getChildren().length != 0)
    {
      currDepth += 1;
      Expr[] kids = cur.getChildren();
      for (int i = 0; i < kids.length; i++) {
        get_depth2(kids[i], target);
      }
      currDepth -= 1;
    }
  }
  
  public static int getDepth2(Expr cur, Expr target)
  {
    depthBetweenNodes = 0;
    currDepth = 0;
    treeDepth = 0;
    get_depth2(cur, target);
    return depthBetweenNodes;
  }
  
  static int treeDepth = 0;
  
  public static void get_depth(Expr node)
  {
    if (node.getChildren().length != 0)
    {
      currDepth += 1;
      if (currDepth > treeDepth) {
        treeDepth = currDepth;
      }
      Expr[] kids = node.getChildren();
      for (int i = 0; i < kids.length; i++) {
        get_depth(kids[i]);
      }
      currDepth -= 1;
    }
  }
  
  public static int getDepth(Expr node)
  {
    currDepth = 0;
    treeDepth = 0;
    get_depth(node);
    return treeDepth;
  }
  
  static Random r = new Random();
  
  public static void traverse(Expr node, ArrayList<Expr> nodes)
  {
    nodes.add(node);
    Expr[] kids = node.getChildren();
    for (int i = 0; i < kids.length; i++) {
      traverse(kids[i], nodes);
    }
  }
  
  public static void traverse2(Expr node, ArrayList<Expr> innerNodes, ArrayList<Expr> terminalNodes)
  {
    if (node.getChildren().length == 0)
    {
      terminalNodes.add(node);
      return;
    }
    innerNodes.add(node);
    Expr[] kids = node.getChildren();
    for (int i = 0; i < kids.length; i++) {
      traverse2(kids[i], innerNodes, terminalNodes);
    }
  }
  
  public static void replace(Expr tn, Expr tOld, Expr tNew)
  {
    Expr[] kids = tn.getChildren();
    for (int i = 0; i < kids.length; i++)
    {
      if (kids[i] == tOld)
      {
        kids[i] = tNew;
        return;
      }
      replace(kids[i], tOld, tNew);
    }
  }
  
  public static void replace2(Expr tn, Expr tOld, Expr tNew)
  {
    Function f = (Function)tn;
    Funcall func = (Funcall)f.func;
    Expr impl = func.impl;
    if (impl == tOld) {
      ((Funcall)f.func).impl = tNew;
    } else {
      replace(impl, tOld, tNew);
    }
  }
}
