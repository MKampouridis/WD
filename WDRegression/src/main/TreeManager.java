package main;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 */

public class TreeManager implements MethodSet{
	public ArrayList<?> methodSet;
	public ArrayList<?> terminalSet;
	static Random r = new Random();
	public double primProb;
	TypeManager typeManager;
	int maxInitialDepth;
	int maxDepth;
	double terminalNodeCrossoverBias; //the probability of selecting a leaf node in crossover operator


	public TreeManager(ArrayList<?> methodSet, ArrayList<?> terminalSet, double primProb,
			int maxInitialDepth, int maxDepth, double terminalNodeCrossoverBias) {
		this.methodSet = methodSet;
		this.terminalSet = terminalSet;
		this.primProb = primProb;
		this.maxInitialDepth = maxInitialDepth;
		this.maxDepth = maxDepth;
		this.terminalNodeCrossoverBias = terminalNodeCrossoverBias;
		this.typeManager = new TypeManager(maxDepth, methodSet, terminalSet);
	}
	static String firstChild;
	static Function evolvedMethod = new Function(boolean.class, new Class[]{double.class, double.class}); //to be modified .........
	static Expr[] evolvedMethodParameters = new Expr[]{};


	/**
	 * Full method in tree generation
	 *
	 * Select the root randomly from the function set and then continue recursively
	 * to select randomly from the function set until the maximum allowable
	 * depth is reached at which point a node is being selected from the
	 * terminal set.
	 *
	 * @param depth The depth of the tree we want to generate
	 * @return a reference to the root node
	 */

	  public Expr makeTreeFullMethod(int depth){
	    if(depth < 1){
	      return (Expr)terminalSet.get(r.nextInt(terminalSet.size()));
	    }
	    Expr toReturn = null;
	    Expr[] children = {};
	    //get a random element from the function set
	    //Object o = methodSet.get(r.nextInt(methodSet.size()));
            Object o = methodSet.get(0);
            System.out.println(o);
	    //if the random method is an object of the MethodCall wrapper of the primitive methods
	    if(o instanceof MethodCall){
	      MethodCall methCall = (MethodCall) o;
	      System.out.println("Choosing a MethodCall: "+methCall);
	      // now fill its arguments in...
	      Class<?>[] parameters = methCall.method.getParameterTypes();
	      children = new Expr[parameters.length];
	      toReturn = new Function(methCall, children);
	    }
	    else if(o instanceof Function){
	      System.out.println("Choosing recursion");
	      Function f = (Function) o;
	      children = new Expr[f.arity];
	      toReturn = new Function(new Funcall(f), children);
	    }
	    else if(o instanceof If){
	      System.out.println("Choosing if");
	      children = new Expr[3];  //we assume we have three branches and set it statically
	      toReturn = new If(children);
	    }
	    for(int i=0; i<children.length; i++){
	      children[i] = makeTreeFullMethod(depth-1);
	    }
	    return toReturn;
	  }
	
	public Expr makeTypedTreeFullMethod(int depth, Class<?> type){
		Expr[] children = {};
		if(depth < 1){
			//get the array list of the terminal elements of the appropriate type
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);

			return getTypedTerminal(type,termList);
		}
		Expr toReturn = null;

		Class<?>[] parameterTypes = {};

		//get the array list of the function elements of the appropriate type
		ArrayList<?> funcList = (ArrayList<?>)typeManager.typedFunctions.get(type);

		if(funcList == null){
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);
			return getTypedTerminal(type,termList);
		}
		//get possible elements for the given depth
		ArrayList<?> possibleElements = (ArrayList<?>)typeManager.elementPossibilityFull.get(new Integer(depth));
		//get possible typed elements (type matches and element is plausible for the specified depth)
		ArrayList<?> possibleTypedElements = (ArrayList<?>)typeManager.getPossibleTypedElements(funcList, possibleElements);
		//get a random object

		if(possibleTypedElements.size() == 0){
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);
			return getTypedTerminal(type,termList);
		}

		Object o = possibleTypedElements.get(r.nextInt(possibleTypedElements.size()));

		if(o instanceof MethodCall){
			MethodCall methCall = (MethodCall)o;
			parameterTypes = methCall.method.getParameterTypes();
			children = new Expr[parameterTypes.length];
			toReturn = new Function(methCall, children, type, parameterTypes);
		}
		else if(o instanceof Function){
			Function f = (Function)o;
			parameterTypes = f.parameterTypes;
			children = new Expr[parameterTypes.length];
			toReturn = new Function(new Funcall(f), children, type, parameterTypes);
		}
		else if(o instanceof If){
			If ifExpr = (If)o;
			parameterTypes = ifExpr.branchesTypes;
			children = new Expr[parameterTypes.length];
			toReturn = new If(children, type, parameterTypes);
		}

		for(int i = 0; i < children.length; i++){
			Class<?> treeType = parameterTypes[i];
			children[i] = makeTypedTreeFullMethod(depth-1, treeType);
		}
		return toReturn;
	}

	/**
	 * Grow method in tree generation
	 *
	 * The root is always chosen form the function set. We continue
	 * recursively by selecting children from both the function and
	 * terminal sets until either a terminal is selected or the maximum
	 * depth is reached (at which point a terminal is always selected)
	 *
	 * @param depth The maximum depth of the tree
	 * @return a reference to the root node
	 */
	public Expr makeTreeGrowMethod(int depth, Class<?> type){
		if(depth < 1 || r.nextDouble() < this.primProb){
			Expr term = (Expr)terminalSet.get(r.nextInt(terminalSet.size()));
			return term;
		}
		Expr toReturn = null;
		Expr[] children = {};
		//get a random element from the function set
                Object o = methodSet.get(0);
		//Object o = methodSet.get(r.nextInt(methodSet.size()));
		//if the random method is an object of the MethodCall wrapper of the primitive methods
		if(o instanceof MethodCall){
			//get its Class
			MethodCall methCall = (MethodCall) o;
			System.out.println("Choosing a MethodCall: " + methCall);
			// now fill its arguments in...
			Class<?>[] parameters = methCall.method.getParameterTypes();
			children = new Expr[parameters.length];
			toReturn = new Function(methCall, children);
		}
		else if(o instanceof Function){
			System.out.println("Choosing recursion");
			Function f = (Function) o;
			children = new Expr[f.arity];
			toReturn = new Function(new Funcall(f), children);
		}

		for(int i=0; i<children.length; i++){
			children[i] = makeTreeGrowMethod(depth-1, type);
		}
		return toReturn;
	}



	public Expr makeTypedTreeGrowMethod(int depth, Class<?> type){

		if(depth < 1){
			//get the array list of the terminal elements of the appropriate type
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);
			return getTypedTerminal(type,termList);
		}

		if(r.nextDouble() < this.primProb){
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);
			if(termList != null){
				return getTypedTerminal(type,termList);
			}
		}

		Expr toReturn = null;
		Expr[] children = {};
		Class<?>[] parameterTypes = {};

		//get the array list of the function elements of the appropriate type
		ArrayList<?> funcList = (ArrayList<?>)typeManager.typedFunctions.get(type);
		if(funcList == null){
			//get the array list of the terminal elements of the appropriate type
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);

			return getTypedTerminal(type,termList);
		}
		//get possible elements for the given depth
		ArrayList<?> possibleElements = (ArrayList<?>)typeManager.elementPossibilityGrow.get(new Integer(depth));
		//get possible typed elements (type matches and element is plusible for the specified depth)
		ArrayList<?> possibleTypedElements = (ArrayList<?>)typeManager.getPossibleTypedElements(funcList, possibleElements);
		//get a random object

		if(possibleTypedElements.size() == 0){
			ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);

			return getTypedTerminal(type,termList);
		}

		Object o = possibleTypedElements.get(r.nextInt(possibleTypedElements.size()));

		if(o instanceof MethodCall){
			MethodCall methCall = (MethodCall)o;
			parameterTypes = methCall.method.getParameterTypes();
			children = new Expr[parameterTypes.length];
			toReturn = new Function(methCall, children, type, parameterTypes);
		}
		else if(o instanceof Function){
			Function f = (Function)o;
			parameterTypes = f.parameterTypes;
			children = new Expr[parameterTypes.length];
			toReturn = new Function(new Funcall(f), children, type, parameterTypes);
		}
		else if(o instanceof If){
			If ifExpr = (If)o;
			parameterTypes = ifExpr.branchesTypes;
			children = new Expr[parameterTypes.length];
			toReturn = new If(children, type, parameterTypes);
		}
		for(int i=0; i<children.length; i++){
			Class<?> treeType = parameterTypes[i];
			children[i] = makeTypedTreeGrowMethod(depth-1, treeType);
		}
		return toReturn;
	}

	/**
	 * Method that returns a typed node from the terminal set.
	 * Nore that the terminal set may also contain non-argument methods
	 * @param type Class The type of the returned node
	 * @return Expr The returned node
	 */
	public static Expr getTypedTerminal(Class<?> type, ArrayList<?> termList){
		//get a random element
		Object term = termList.get(r.nextInt(termList.size()));

		if (term instanceof MethodCall) {
			return new Function((MethodCall) term, new Expr[] {}, type, new Class[] {});
		} 
		else if (term instanceof Function) {
			return new Function(new Funcall((Function) term), new Expr[] {}, type,
					new Class[] {});
		} 
		else {
			return ((Expr) term).copy(new HashSet<Object>(), new ArrayList<Object>());
		}
	}

	//Method that implements the Ramped-half-half method of tree generation
	public Evaluated[] initialPopulationRampedHalf(Evaluator eval, int popSize){
		popRampedHalfCursor = 0;
		Evaluated[] initPop = new Evaluated[popSize];
		int sizeClasses = this.maxInitialDepth;
		int noOfIndividuals = popSize / sizeClasses; //this was initially at alex's version, but has been removed
		//since im not using ramped half hypothesis, coz i only need grow method, not full method.
		//      int noOfIndividuals = popSize;
		for(int i = 3; i <= sizeClasses; i++){
			initRampedHalfHypothesis(eval, noOfIndividuals, initPop, i);
		}
		//now fill any remainning slots
		while(popRampedHalfCursor < popSize){
			int randGenMethod = r.nextInt(2) + 1;
			int randDepth = r.nextInt(sizeClasses) + 3;
			initPop[popRampedHalfCursor++] = new Evaluated(generateTypedTree(randDepth, evolvedMethod.getType(), randGenMethod));
		}
		return initPop;
	}

	static int popRampedHalfCursor;
	public void initRampedHalfHypothesis(Evaluator eval, int noOfIndividuals, Evaluated[] pop, int rampedDepth)
	{
		for(int i = 0; i < noOfIndividuals; i++)
		{
			if(i%2 == 0)
			{
				//full method in tree generation
				pop[popRampedHalfCursor++] = new Evaluated(generateTypedTree(rampedDepth, evolvedMethod.getType(), 1));
			}
			else
			{
				//grow method in tree generation
				pop[popRampedHalfCursor++] = new Evaluated(generateTypedTree(rampedDepth, evolvedMethod.getType(), 2));
			}
		}
	}

	/**
	 * Method that returns the root node from the function set
	 * @return Expr The reference to the root node
	 */
	//   public Expr getRootNode() {
	//       Expr toReturn = null;
	//       Expr[] children = {};
	//       //we only choose from the function set (we exclude the recursive node from the function set)
	//       Object o = methodSet.get(r.nextInt(methodSet.size() - 1));
	//       //if the random method is an object of the MethodCall wrapper of the primitive methods
	//       if (o instanceof MethodCall) {
	//           MethodCall methCall = (MethodCall) o;
	//           System.out.println("Choosing a MethodCall: " + methCall);
	//           // now fill its arguments in...
	//           Class[] parameters = methCall.method.getParameterTypes();
	//           children = new Expr[parameters.length];
	//           toReturn = new Function(methCall, children);
	//       } else if (o instanceof If) {
	//           System.out.println("Choosing if");
	//           children = new Expr[3];
	//           toReturn = new If(children);
	//       }
	//       return toReturn;
	//   }

	/**
	 * This method is used to extend the tree. It is being fed with a root node
	 * and starts producing the tree by first creating the children of the
	 * root node and continuing recursively.
	 *
	 * @param depth int the max depth of the tree
	 * @param root TreeNode the root of the tree
	 * @param genMethod int the tree generation method (full or grow)
	 * 1: full, 2: grow
	 * @return Expr the root node
	 */
	//  public Expr extendTree(int depth, Expr root, int genMethod){
	//    Expr[] children = root.getChildren();
	//    for(int i=0; i<children.length; i++){
	//      if(genMethod == 1){ //full method
	//        children[i] = makeTreeFullMethod(depth-1);
	//      }
	//      else{  //grow method
	//        children[i] = makeTreeGrowMethod(depth-1);
	//      }
	//    }
	//    return root;
	//  }

	/**
	 * Method for generating a type tree. We require that the root of the tree
	 * chosen from the function set
	 * @param depth int The depth of the tree
	 * @param type Class The return type of the whole tree
	 * @param generationMethod int The generation method (1:full 2:grow)
	 * @return Expr The root node of the tree
	 */
	public Expr generateTypedTree(int depth, Class<?> type, int generationMethod){
		Expr tree = null;
		Expr[] children = {};
		Class<?>[] parameterTypes = {};
		//we require that the root node comes from the function set
		//we only choose from the function set and of the appropriate type
		ArrayList<?> funcList = (ArrayList<?>)typeManager.typedFunctions.get(type);

		//System.out.println("Type : "+type + ",  Depth : "+depth+",   "+funcList);


		//////////////////////////////////////////////////
		ArrayList<?> possibleElements = null;
		if(generationMethod == 1){
			if (depth > maxDepth)
				depth = maxDepth;//there's a bug, which ends up with depth higher than the allowed. So i reset it to maxDepth if it happens.
			possibleElements = (ArrayList<?>) typeManager.elementPossibilityFull.get(new Integer(depth));
		}
		else{
			if (depth > maxDepth)
				depth = maxDepth;
			possibleElements = (ArrayList<?>) typeManager.elementPossibilityGrow.get(new Integer(depth));

		}
		@SuppressWarnings("rawtypes")
		ArrayList possibleTypedElements = (ArrayList)typeManager.getPossibleTypedElements(funcList, possibleElements);

		if(possibleTypedElements.size() == 0){
			possibleElements = (ArrayList<?>) typeManager.elementPossibilityGrow.get(new Integer(depth));
			possibleTypedElements = (ArrayList<?>)typeManager.getPossibleTypedElements(funcList, possibleElements);
		}
                
                //THIS IS WHERE THE NODE IS RANDOMLY CHOSEN!!!!!
                
		//Object o = possibleTypedElements.get(r.nextInt(possibleTypedElements.size()));
                Object o = possibleTypedElements.get(2); //This sets the node always to ADD
                
		if(o instanceof MethodCall){
			MethodCall methCall = (MethodCall)o;
			parameterTypes = methCall.method.getParameterTypes();
			children = new Expr[parameterTypes.length];
			tree = new Function(methCall, children, type, parameterTypes);
		}
		else if(o instanceof Function){
			Function f = (Function)o;
			parameterTypes = f.parameterTypes;
			children = new Expr[parameterTypes.length];
			tree = new Function(new Funcall(f), children, type, parameterTypes);
		}
		else if(o instanceof If){
			If ifExpr = (If)o;
			parameterTypes = ifExpr.branchesTypes;
			children = new Expr[parameterTypes.length];
			tree = new If(children, type, parameterTypes);
		}
                
               // children[0] = new Function(methCall, children, type, parameterTypes); 
               // children[1] = new Function(methCall, children, type, parameterTypes); 
                
                // Could force the grow method to only do nodes by changing primProb
                
		//continue and fill the root node's arguments in
                generationMethod = 2; //force grow
		for(int i=0; i<children.length; i++){
			Class<?> subTreeType = parameterTypes[i];
			if(generationMethod == 1){  //full method of random tree generation
				children[i] = makeTypedTreeFullMethod(depth-1, subTreeType);
			}
			else {  //grow method in random tree generation
				children[i] = makeTypedTreeGrowMethod(depth-1, subTreeType);
			}
		}
                
                
                
		Function f = new Function(new Funcall(tree), evolvedMethodParameters);

		return f;


	}

	public Object getIfNode(ArrayList<?> funcList){
		for(int i=0; i<funcList.size(); i++){
			if(funcList.get(i) instanceof If){
				return funcList.get(i);
			}
		}
		System.out.println("Error - Should not be here : TreeManager.getIfNode()");
		return null;
	}

	   //this is equivalent to bit-flip mutation of binary GA
    public Expr point_mutate(Expr parent){
        Expr copied = parent.copy(new HashSet<Object>(), new ArrayList<Object>());
        Function f = (Function)copied;
        Expr impl = ((Funcall)f.func).impl;
        Expr tOld = randSelect(impl);
        Expr tNew = null;
        Class<?> type = tOld.getType();
        //set the arity to the number of its children
        Expr[] kids = tOld.getChildren();
        int arity = kids.length;
        //get the parameter types of its children
        Class<?>[] paramTypes = new Class[arity];
        for(int i=0; i<paramTypes.length; i++){
            paramTypes[i] = kids[i].getType();
        }
        //we are looking at a leaf node
        if(arity == 0){
            ArrayList<?> termList = (ArrayList<?>)typeManager.typedTerminals.get(type);
            tNew = ((Expr)termList.get(r.nextInt(termList.size()))).copy(new HashSet<Object>(), new ArrayList<Object>());
            Util.replace(impl, tOld, tNew); //i use replace and not replace2 because we are replacing a leaf
        }
        else{
            ArrayList<?> typedFuncList = (ArrayList<?>) typeManager.typedFunctions.get(type);
            ArrayList<?> typedArity = (ArrayList<?>) typeManager.getTypedArity(typedFuncList, paramTypes);
            Object o = typedArity.get(r.nextInt(typedArity.size()));
            if(o instanceof MethodCall){
                MethodCall mc = (MethodCall)o;
                tNew = new Function(mc, kids, type, paramTypes);
            }
            else if(o instanceof Function){
                Function fun = (Function)o;
                tNew = new Function(new Funcall(fun), kids, type, paramTypes);
            }
            else if(o instanceof If){
                tNew = new If(kids, type);
            }
//            System.out.println("The new non-terminal node");
//            tNew.print2(System.out, "  ", new HashSet());
            Util.replace2(f, tOld, tNew);
        }
        return f;
    }
	
	//Mutation. Typical mutation (Koza)
	public Expr mutate(Expr parent){
		Expr copied = parent.copy(new HashSet<Object>(), new ArrayList<Object>());
		Function f = (Function)copied;
		Expr impl = ((Funcall)f.func).impl;
		//select a random node from the parent
		Expr tOld = randSelect(impl);
		Class<?> type = tOld.getType();
		Expr tNew = null;
		//choose the tree generation method
		int genMethod = 1;
		if(r.nextDouble() > 0.5){  //0.5
			genMethod = 2;
		}
		if(tOld == impl){
			//create and return a new random tree from scratch
			return generateTypedTree(this.maxDepth, type, genMethod);
		}
		else {
			int selectedDepth=0;
			int maxAllowedDepth = this.maxDepth-Util.getDepth2(impl, tOld);
			
			//get a random depth between 1 and maximum allowed depth
			if(maxAllowedDepth != 0){
				int randomDepth = r.nextInt(maxAllowedDepth) + 1;
				selectedDepth = randomDepth;
			}
			if(genMethod == 1){
				//randomly create a new tree preserving the max depth and type constraints
				tNew = makeTypedTreeFullMethod(selectedDepth, type);
			}
			else{
				tNew = makeTypedTreeGrowMethod(selectedDepth, type);
			}
			Util.replace(impl, tOld, tNew);
			return f;
		}
	}

	public Expr crossover(Expr p1, Expr p2){

		Expr copied1 = p1.copy(new HashSet<Object>(), new ArrayList<Object>());
		Function f1 = (Function)copied1;
		Expr impl1 = ((Funcall)f1.func).impl;
		Expr copied2 = p2.copy(new HashSet<Object>(), new ArrayList<Object>());
		Function f2 = (Function)copied2;
		Expr impl2 = ((Funcall)f2.func).impl;
		//pick a random node from the first parent
		Expr tOld = randSelect2(impl1);
		Class<?> type = tOld.getType();
		int maxAllowedDepth = this.maxDepth - Util.getDepth2(impl1, tOld);
		Expr tNew = randSelect3(impl2, type, maxAllowedDepth);
		if(tNew != null){
			Util.replace2(f1, tOld, tNew);

		}
		return f1;
	}

	/**
	 * This method performs a uniform random selection over all nodes including
	 * the one passed as a parameter
	 * @param tree Expr The root node of the tree
	 * @return Expr The selected node
	 */

	public Expr randSelect(Expr tree){
		ArrayList<Expr> nodes = new ArrayList<Expr>();
		Util.traverse(tree, nodes);
		Expr node = (Expr) nodes.get(r.nextInt(nodes.size()));
		return node;
	}

	/**
	 * Method for selecting a random node from the first parent for crossover.
	 * The selection is biased towards selecting an inner node with a probability
	 * of terminalNodeCrossBias
	 * @param tn TreeNode
	 * @return TreeNode
	 */
	public Expr randSelect2(Expr tree){
		ArrayList<Expr> innerNodes = new ArrayList<Expr>();
		ArrayList<Expr> terminalNodes = new ArrayList<Expr>();
		Util.traverse2(tree, innerNodes, terminalNodes);
		Expr randomNode = null;
		if(r.nextDouble() < this.terminalNodeCrossoverBias){  //select a leaf node
			randomNode = (Expr) terminalNodes.get(r.nextInt(terminalNodes.size()));
		}
		else { //select an inner node
			if(innerNodes.size() != 0){
				randomNode = (Expr) innerNodes.get(r.nextInt(innerNodes.size()));
			} else { //in case of a tree of a single non-argument method
				randomNode = (Expr) terminalNodes.get(r.nextInt(terminalNodes.size()));
			}
		}
		return randomNode;
	}

	/**
	 * Method for selecting the random node form the second parent.
	 * There are a couple of constraints that need to be satisfied:
	 * 1) The type of the subtree
	 * 2) The depth of the subtree
	 * If is possible to select a valid subtree from this parent, it returns null
	 * @param tree The second parent of the crossover
	 * @param type The type of the subtree required
	 * @param maxAllowedDepth Max depth, non-bloated crossover
	 * @return The selected subtree
	 */
	public Expr randSelect3(Expr tree, Class<?> type, int maxAllowedDepth){
		ArrayList<Expr> innerNodes = new ArrayList<Expr>();
		ArrayList<Expr> terminalNodes = new ArrayList<Expr>();
		Util.traverse2(tree, innerNodes, terminalNodes);
		//organise the function and terminal nodes into HashMaps
		//the key being the expression's type and the value being tha array list with the appropriate elements
		HashMap<?, ?> typedFunctions = typeManager.makeTypedFunctions(innerNodes);
		HashMap<?, ?> typedTerminals = typeManager.makeTypedTerminals(terminalNodes);

		if(maxAllowedDepth == 0){  //a terminal node is required
			ArrayList<?> termList = (ArrayList<?>)typedTerminals.get(type);
			if(termList != null){
				return getTypedTerminal(type, termList);
			}
			else {
				return null;
			}
		}
		else {
			ArrayList<?> funcList = (ArrayList<?>)typedFunctions.get(type);
			if(funcList != null){
				ArrayList<Expr> nonBloatedNodes = getNonBloatedNodes(funcList, maxAllowedDepth);
				if(nonBloatedNodes.size() != 0){
					return nonBloatedNodes.get(r.nextInt(nonBloatedNodes.size()));
				}
				else {
					return null;
				}
			}
			else {
				return null;
			}
		}
	}

	public ArrayList<Expr> getNonBloatedNodes(Collection<?> functionNodes, int allowedDepth){
		ArrayList<Expr> nonBloatedNodes = new ArrayList<Expr>();
		for(Iterator<?> i=functionNodes.iterator(); i.hasNext();){
			Expr node = (Expr)i.next();
			if(Util.getDepth(node) <= allowedDepth){
				nonBloatedNodes.add(node);
			}
		}
		return nonBloatedNodes;
	}

}
