package main;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class TreeManager
  implements MethodSet
{
  public ArrayList<?> methodSet;
  public ArrayList<?> terminalSet;
  static Random r = new Random();
  public double primProb;
  TypeManager typeManager;
  int maxInitialDepth;
  int maxDepth;
  double terminalNodeCrossoverBias;
  static String firstChild;
  
  public TreeManager(ArrayList<?> methodSet, ArrayList<?> terminalSet, double primProb, int maxInitialDepth, int maxDepth, double terminalNodeCrossoverBias)
  {
    this.methodSet = methodSet;
    this.terminalSet = terminalSet;
    this.primProb = primProb;
    this.maxInitialDepth = maxInitialDepth;
    this.maxDepth = maxDepth;
    this.terminalNodeCrossoverBias = terminalNodeCrossoverBias;
    this.typeManager = new TypeManager(maxDepth, methodSet, terminalSet);
  }
  
  static Function evolvedMethod = new Function(Boolean.TYPE, new Class[] { Double.TYPE, Double.TYPE });
  static Expr[] evolvedMethodParameters = new Expr[0];
  static int popRampedHalfCursor;
  
  public Expr makeTreeFullMethod(int depth)
  {
    if (depth < 1) {
      return (Expr)this.terminalSet.get(r.nextInt(this.terminalSet.size()));
    }
    Expr toReturn = null;
    Expr[] children = new Expr[0];
    
    Object o = this.methodSet.get(r.nextInt(this.methodSet.size()));
    if ((o instanceof MethodCall))
    {
      MethodCall methCall = (MethodCall)o;
      System.out.println("Choosing a MethodCall: " + methCall);
      
      Class<?>[] parameters = methCall.method.getParameterTypes();
      children = new Expr[parameters.length];
      toReturn = new Function(methCall, children);
    }
    else if ((o instanceof Function))
    {
      System.out.println("Choosing recursion");
      Function f = (Function)o;
      children = new Expr[f.arity];
      toReturn = new Function(new Funcall(f), children);
    }
    else if ((o instanceof If))
    {
      System.out.println("Choosing if");
      children = new Expr[3];
      toReturn = new If(children);
    }
    for (int i = 0; i < children.length; i++) {
      children[i] = makeTreeFullMethod(depth - 1);
    }
    return toReturn;
  }
  
  public Expr makeTypedTreeFullMethod(int depth, Class<?> type)
  {
    Expr[] children = new Expr[0];
    if (depth < 1)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      
      return getTypedTerminal(type, termList);
    }
    Expr toReturn = null;
    
    Class<?>[] parameterTypes = new Class[0];
    

    ArrayList<?> funcList = (ArrayList)this.typeManager.typedFunctions.get(type);
    if (funcList == null)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      return getTypedTerminal(type, termList);
    }
    ArrayList<?> possibleElements = (ArrayList)this.typeManager.elementPossibilityFull.get(new Integer(depth));
    
    ArrayList<?> possibleTypedElements = (ArrayList)this.typeManager.getPossibleTypedElements(funcList, possibleElements);
    if (possibleTypedElements.size() == 0)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      return getTypedTerminal(type, termList);
    }
    Object o = possibleTypedElements.get(r.nextInt(possibleTypedElements.size()));
    if ((o instanceof MethodCall))
    {
      MethodCall methCall = (MethodCall)o;
      parameterTypes = methCall.method.getParameterTypes();
      children = new Expr[parameterTypes.length];
      toReturn = new Function(methCall, children, type, parameterTypes);
    }
    else if ((o instanceof Function))
    {
      Function f = (Function)o;
      parameterTypes = f.parameterTypes;
      children = new Expr[parameterTypes.length];
      toReturn = new Function(new Funcall(f), children, type, parameterTypes);
    }
    else if ((o instanceof If))
    {
      If ifExpr = (If)o;
      parameterTypes = ifExpr.branchesTypes;
      children = new Expr[parameterTypes.length];
      toReturn = new If(children, type, parameterTypes);
    }
    for (int i = 0; i < children.length; i++)
    {
      Class<?> treeType = parameterTypes[i];
      children[i] = makeTypedTreeFullMethod(depth - 1, treeType);
    }
    return toReturn;
  }
  
  public Expr makeTreeGrowMethod(int depth, Class<?> type)
  {
    if ((depth < 1) || (r.nextDouble() < this.primProb))
    {
      Expr term = (Expr)this.terminalSet.get(r.nextInt(this.terminalSet.size()));
      return term;
    }
    Expr toReturn = null;
    Expr[] children = new Expr[0];
    
    Object o = this.methodSet.get(r.nextInt(this.methodSet.size()));
    if ((o instanceof MethodCall))
    {
      MethodCall methCall = (MethodCall)o;
      System.out.println("Choosing a MethodCall: " + methCall);
      
      Class<?>[] parameters = methCall.method.getParameterTypes();
      children = new Expr[parameters.length];
      toReturn = new Function(methCall, children);
    }
    else if ((o instanceof Function))
    {
      System.out.println("Choosing recursion");
      Function f = (Function)o;
      children = new Expr[f.arity];
      toReturn = new Function(new Funcall(f), children);
    }
    for (int i = 0; i < children.length; i++) {
      children[i] = makeTreeGrowMethod(depth - 1, type);
    }
    return toReturn;
  }
  
  public Expr makeTypedTreeGrowMethod(int depth, Class<?> type)
  {
    if (depth < 1)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      return getTypedTerminal(type, termList);
    }
    if (r.nextDouble() < this.primProb)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      if (termList != null) {
        return getTypedTerminal(type, termList);
      }
    }
    Expr toReturn = null;
    Expr[] children = new Expr[0];
    Class<?>[] parameterTypes = new Class[0];
    

    ArrayList<?> funcList = (ArrayList)this.typeManager.typedFunctions.get(type);
    if (funcList == null)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      
      return getTypedTerminal(type, termList);
    }
    ArrayList<?> possibleElements = (ArrayList)this.typeManager.elementPossibilityGrow.get(new Integer(depth));
    
    ArrayList<?> possibleTypedElements = (ArrayList)this.typeManager.getPossibleTypedElements(funcList, possibleElements);
    if (possibleTypedElements.size() == 0)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      
      return getTypedTerminal(type, termList);
    }
    Object o = possibleTypedElements.get(r.nextInt(possibleTypedElements.size()));
    if ((o instanceof MethodCall))
    {
      MethodCall methCall = (MethodCall)o;
      parameterTypes = methCall.method.getParameterTypes();
      children = new Expr[parameterTypes.length];
      toReturn = new Function(methCall, children, type, parameterTypes);
    }
    else if ((o instanceof Function))
    {
      Function f = (Function)o;
      parameterTypes = f.parameterTypes;
      children = new Expr[parameterTypes.length];
      toReturn = new Function(new Funcall(f), children, type, parameterTypes);
    }
    else if ((o instanceof If))
    {
      If ifExpr = (If)o;
      parameterTypes = ifExpr.branchesTypes;
      children = new Expr[parameterTypes.length];
      toReturn = new If(children, type, parameterTypes);
    }
    for (int i = 0; i < children.length; i++)
    {
      Class<?> treeType = parameterTypes[i];
      children[i] = makeTypedTreeGrowMethod(depth - 1, treeType);
    }
    return toReturn;
  }
  
  public static Expr getTypedTerminal(Class<?> type, ArrayList<?> termList)
  {
    Object term = termList.get(r.nextInt(termList.size()));
    if ((term instanceof MethodCall)) {
      return new Function((MethodCall)term, new Expr[0], type, new Class[0]);
    }
    if ((term instanceof Function)) {
      return new Function(new Funcall((Function)term), new Expr[0], type, new Class[0]);
    }
    return ((Expr)term).copy(new HashSet(), new ArrayList());
  }
  
  public Evaluated[] initialPopulationRampedHalf(Evaluator eval, int popSize)
  {
    popRampedHalfCursor = 0;
    Evaluated[] initPop = new Evaluated[popSize];
    int sizeClasses = this.maxInitialDepth;
    int noOfIndividuals = popSize / sizeClasses;
    for (int i = 3; i <= sizeClasses; i++) {
      initRampedHalfHypothesis(eval, noOfIndividuals, initPop, i);
    }
    while (popRampedHalfCursor < popSize)
    {
      int randGenMethod = r.nextInt(2) + 1;
      int randDepth = r.nextInt(sizeClasses) + 3;
      initPop[(popRampedHalfCursor++)] = new Evaluated(generateTypedTree(randDepth, evolvedMethod.getType(), randGenMethod));
    }
    return initPop;
  }
  
  public void initRampedHalfHypothesis(Evaluator eval, int noOfIndividuals, Evaluated[] pop, int rampedDepth)
  {
    for (int i = 0; i < noOfIndividuals; i++) {
      if (i % 2 == 0) {
        pop[(popRampedHalfCursor++)] = new Evaluated(generateTypedTree(rampedDepth, evolvedMethod.getType(), 1));
      } else {
        pop[(popRampedHalfCursor++)] = new Evaluated(generateTypedTree(rampedDepth, evolvedMethod.getType(), 2));
      }
    }
  }
  
  public Expr generateTypedTree(int depth, Class<?> type, int generationMethod)
  {
    Expr tree = null;
    Expr[] children = new Expr[0];
    Class<?>[] parameterTypes = new Class[0];
    

    ArrayList<?> funcList = (ArrayList)this.typeManager.typedFunctions.get(type);
    




    ArrayList<?> possibleElements = null;
    if (generationMethod == 1)
    {
      if (depth > this.maxDepth) {
        depth = this.maxDepth;
      }
      possibleElements = (ArrayList)this.typeManager.elementPossibilityFull.get(new Integer(depth));
    }
    else
    {
      if (depth > this.maxDepth) {
        depth = this.maxDepth;
      }
      possibleElements = (ArrayList)this.typeManager.elementPossibilityGrow.get(new Integer(depth));
    }
    ArrayList possibleTypedElements = (ArrayList)this.typeManager.getPossibleTypedElements(funcList, possibleElements);
    if (possibleTypedElements.size() == 0)
    {
      possibleElements = (ArrayList)this.typeManager.elementPossibilityGrow.get(new Integer(depth));
      possibleTypedElements = (ArrayList)this.typeManager.getPossibleTypedElements(funcList, possibleElements);
    }
    Object o = possibleTypedElements.get(r.nextInt(possibleTypedElements.size()));
    if ((o instanceof MethodCall))
    {
      MethodCall methCall = (MethodCall)o;
      parameterTypes = methCall.method.getParameterTypes();
      children = new Expr[parameterTypes.length];
      tree = new Function(methCall, children, type, parameterTypes);
    }
    else if ((o instanceof Function))
    {
      Function f = (Function)o;
      parameterTypes = f.parameterTypes;
      children = new Expr[parameterTypes.length];
      tree = new Function(new Funcall(f), children, type, parameterTypes);
    }
    else if ((o instanceof If))
    {
      If ifExpr = (If)o;
      parameterTypes = ifExpr.branchesTypes;
      children = new Expr[parameterTypes.length];
      tree = new If(children, type, parameterTypes);
    }
    for (int i = 0; i < children.length; i++)
    {
      Class<?> subTreeType = parameterTypes[i];
      if (generationMethod == 1) {
        children[i] = makeTypedTreeFullMethod(depth - 1, subTreeType);
      } else {
        children[i] = makeTypedTreeGrowMethod(depth - 1, subTreeType);
      }
    }
    Function f = new Function(new Funcall(tree), evolvedMethodParameters);
    
    return f;
  }
  
  public Object getIfNode(ArrayList<?> funcList)
  {
    for (int i = 0; i < funcList.size(); i++) {
      if ((funcList.get(i) instanceof If)) {
        return funcList.get(i);
      }
    }
    System.out.println("Error - Should not be here : TreeManager.getIfNode()");
    return null;
  }
  
  public Expr point_mutate(Expr parent)
  {
    Expr copied = parent.copy(new HashSet(), new ArrayList());
    Function f = (Function)copied;
    Expr impl = ((Funcall)f.func).impl;
    Expr tOld = randSelect(impl);
    Expr tNew = null;
    Class<?> type = tOld.getType();
    
    Expr[] kids = tOld.getChildren();
    int arity = kids.length;
    
    Class<?>[] paramTypes = new Class[arity];
    for (int i = 0; i < paramTypes.length; i++) {
      paramTypes[i] = kids[i].getType();
    }
    if (arity == 0)
    {
      ArrayList<?> termList = (ArrayList)this.typeManager.typedTerminals.get(type);
      tNew = ((Expr)termList.get(r.nextInt(termList.size()))).copy(new HashSet(), new ArrayList());
      Util.replace(impl, tOld, tNew);
    }
    else
    {
      ArrayList<?> typedFuncList = (ArrayList)this.typeManager.typedFunctions.get(type);
      ArrayList<?> typedArity = (ArrayList)this.typeManager.getTypedArity(typedFuncList, paramTypes);
      Object o = typedArity.get(r.nextInt(typedArity.size()));
      if ((o instanceof MethodCall))
      {
        MethodCall mc = (MethodCall)o;
        tNew = new Function(mc, kids, type, paramTypes);
      }
      else if ((o instanceof Function))
      {
        Function fun = (Function)o;
        tNew = new Function(new Funcall(fun), kids, type, paramTypes);
      }
      else if ((o instanceof If))
      {
        tNew = new If(kids, type);
      }
      Util.replace2(f, tOld, tNew);
    }
    return f;
  }
  
  public Expr mutate(Expr parent)
  {
    Expr copied = parent.copy(new HashSet(), new ArrayList());
    Function f = (Function)copied;
    Expr impl = ((Funcall)f.func).impl;
    
    Expr tOld = randSelect(impl);
    Class<?> type = tOld.getType();
    Expr tNew = null;
    
    int genMethod = 1;
    if (r.nextDouble() > 0.5D) {
      genMethod = 2;
    }
    if (tOld == impl) {
      return generateTypedTree(this.maxDepth, type, genMethod);
    }
    int selectedDepth = 0;
    int maxAllowedDepth = this.maxDepth - Util.getDepth2(impl, tOld);
    if (maxAllowedDepth != 0)
    {
      int randomDepth = r.nextInt(maxAllowedDepth) + 1;
      selectedDepth = randomDepth;
    }
    if (genMethod == 1) {
      tNew = makeTypedTreeFullMethod(selectedDepth, type);
    } else {
      tNew = makeTypedTreeGrowMethod(selectedDepth, type);
    }
    Util.replace(impl, tOld, tNew);
    return f;
  }
  
  public Expr crossover(Expr p1, Expr p2)
  {
    Expr copied1 = p1.copy(new HashSet(), new ArrayList());
    Function f1 = (Function)copied1;
    Expr impl1 = ((Funcall)f1.func).impl;
    Expr copied2 = p2.copy(new HashSet(), new ArrayList());
    Function f2 = (Function)copied2;
    Expr impl2 = ((Funcall)f2.func).impl;
    
    Expr tOld = randSelect2(impl1);
    Class<?> type = tOld.getType();
    int maxAllowedDepth = this.maxDepth - Util.getDepth2(impl1, tOld);
    Expr tNew = randSelect3(impl2, type, maxAllowedDepth);
    if (tNew != null) {
      Util.replace2(f1, tOld, tNew);
    }
    return f1;
  }
  
  public Expr randSelect(Expr tree)
  {
    ArrayList<Expr> nodes = new ArrayList();
    Util.traverse(tree, nodes);
    Expr node = (Expr)nodes.get(r.nextInt(nodes.size()));
    return node;
  }
  
  public Expr randSelect2(Expr tree)
  {
    ArrayList<Expr> innerNodes = new ArrayList();
    ArrayList<Expr> terminalNodes = new ArrayList();
    Util.traverse2(tree, innerNodes, terminalNodes);
    Expr randomNode = null;
    if (r.nextDouble() < this.terminalNodeCrossoverBias) {
      randomNode = (Expr)terminalNodes.get(r.nextInt(terminalNodes.size()));
    } else if (innerNodes.size() != 0) {
      randomNode = (Expr)innerNodes.get(r.nextInt(innerNodes.size()));
    } else {
      randomNode = (Expr)terminalNodes.get(r.nextInt(terminalNodes.size()));
    }
    return randomNode;
  }
  
  public Expr randSelect3(Expr tree, Class<?> type, int maxAllowedDepth)
  {
    ArrayList<Expr> innerNodes = new ArrayList();
    ArrayList<Expr> terminalNodes = new ArrayList();
    Util.traverse2(tree, innerNodes, terminalNodes);
    

    HashMap<?, ?> typedFunctions = this.typeManager.makeTypedFunctions(innerNodes);
    HashMap<?, ?> typedTerminals = this.typeManager.makeTypedTerminals(terminalNodes);
    if (maxAllowedDepth == 0)
    {
      ArrayList<?> termList = (ArrayList)typedTerminals.get(type);
      if (termList != null) {
        return getTypedTerminal(type, termList);
      }
      return null;
    }
    ArrayList<?> funcList = (ArrayList)typedFunctions.get(type);
    if (funcList != null)
    {
      ArrayList<Expr> nonBloatedNodes = getNonBloatedNodes(funcList, maxAllowedDepth);
      if (nonBloatedNodes.size() != 0) {
        return (Expr)nonBloatedNodes.get(r.nextInt(nonBloatedNodes.size()));
      }
      return null;
    }
    return null;
  }
  
  public ArrayList<Expr> getNonBloatedNodes(Collection<?> functionNodes, int allowedDepth)
  {
    ArrayList<Expr> nonBloatedNodes = new ArrayList();
    for (Iterator<?> i = functionNodes.iterator(); i.hasNext();)
    {
      Expr node = (Expr)i.next();
      if (Util.getDepth(node) <= allowedDepth) {
        nonBloatedNodes.add(node);
      }
    }
    return nonBloatedNodes;
  }
}
