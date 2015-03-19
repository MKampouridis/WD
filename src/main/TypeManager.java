package main;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>Title: TypeManager</p>
 * <p>Description: This class is responsible for managing the strong typing
 * required in the tree implementation</p>
 * @author Alexandros Agapitos (July 2005)
 * @version 1.0
 */
public class TypeManager implements MethodSet{

    HashMap<Class<?>, ArrayList<Object>> typedFunctions; //holds the function array lists, keyed on the function return type
    HashMap typedTerminals; //holds the terminal array lists, keyed on the terminal type
    HashMap elementPossibility; //element possibility table , based on montana's STGP
    static HashMap methodHolders;  //maps the method name to the MethodCall reference

    HashMap typePossibilityGrow;
    HashMap typePossibilityFull;
    HashMap elementPossibilityGrow;
    HashMap elementPossibilityFull;

    static Function evolvedMethod = new Function(double.class, new Class[]{double.class});

    static HashMap wrappers;
    static {
        wrappers = new HashMap();
        wrappers.put(Integer.class, int.class);
        wrappers.put(Double.class, double.class);
    }

    public TypeManager(ArrayList functions, ArrayList terminals, int maxDepth) {
        typedFunctions = makeTypedFunctions(functions);
        typedTerminals = makeTypedTerminals(terminals);
        augmentTypedTerminals(typedTerminals, functions);
        augmentTerminals(functions, terminals);
        elementPossibility = makeElementPossibilityTable(makeTypePossibilityTable(maxDepth,functions,terminals),
                maxDepth, functions);
        methodHolders = makeMethodHolders(functions);
    }

    public TypeManager(int maxDepth, ArrayList functions, ArrayList terminals){
        typedFunctions = makeTypedFunctions(functions);
        augmentTerminals(functions, terminals);
        typedTerminals = makeTypedTerminals2(terminals);
        makeTypePossibilityTables(maxDepth, functions, terminals);
        elementPossibilityGrow = makeElementPossibilityTable(typePossibilityGrow, maxDepth, functions);
        System.out.println("Element Possiblity Grow:");
        System.out.println(elementPossibilityGrow);
        elementPossibilityFull = makeElementPossibilityTable(typePossibilityFull, maxDepth, functions);
        System.out.println("Element Possiblity Full:");
        System.out.println(elementPossibilityFull);
    }

    public TypeManager(Collection functions, Collection terminals){
        typedFunctions = makeTypedFunctions(functions);
        typedTerminals = makeTypedTerminals(terminals);
        methodHolders = makeMethodHolders(functions);
    }

    public TypeManager(){

    }

    //FOR DEBUGGING AND TESTING PURPOSES ONLY
    public static void main(String[] args){
        ArrayList funcs = new ArrayList();
        funcs.add(ADD);
        funcs.add(SUB);
        funcs.add(evolvedMethod);
        funcs.add(new If(double.class, new Class[]{boolean.class, double.class, double.class}));
        funcs.add(MUL);
        funcs.add(DIV);


        ArrayList terms = new ArrayList();
        //terms.add(new Constant(new Double(20), double.class));
        terms.add(new Constant(new Double(40), Double.class));
        //terms.add(new Constant(null, Object.class));

        //terms.add(new Constant(new Boolean(true), boolean.class));
        //terms.add(new Parameter(0, double.class));

        TypeManager man = new TypeManager();
        HashMap m = man.makePolymorphicTerminals(terms);
        System.out.println(m);

    }

    /**
     * This method organises the elements of the function set in the typedFunctions
     * Hash map. The map is keyed on the class/type of the return values of each element
     * of the function set. The value of each key holds the reference to the array list
     * that holds the typed methods.
     * @param functions Collection A collection that holds the function set
     * @return HashMap The mapping from class/type to the corresponding methods
     */
    public HashMap makeTypedFunctions(Collection functions) {
        HashMap map = new HashMap();
        Class returnType = null;
        Iterator i = functions.iterator();
        while(i.hasNext()){
            Object o = i.next();
            if(o instanceof MethodCall){
                returnType = ((MethodCall)o).method.getReturnType();
            }
            else if(o instanceof Function){
                returnType = ((Function)o).returnType;
            }
            else if(o instanceof If){
                returnType = ((If)o).returnType;
            }
            ArrayList list = (ArrayList)map.get(returnType);
            if(list == null){
                list = new ArrayList();
                map.put(returnType, list);
            }
            list.add(o);
        }
        return map;
    }

    /**
    * This method organises the elements of the terminal set in the typedTerminals
    * Hash map. The map is keyed on the class/type of the type of each element
    * of the terminal set. The value of each key holds the reference to the array list
    * that holds the typed terminals.
    * @param functions Collection A collection that holds the terminal set set
    * @return HashMap The mapping from class/type to the corrensponding terminals
    */
    public HashMap makeTypedTerminals(Collection terminals){
        HashMap map = new HashMap();
        Class type = null;
        Iterator i = terminals.iterator();
        while(i.hasNext()){
            Object o = i.next();
            Expr e = (Expr)o;
            type = e.getType();
            ArrayList list = (ArrayList)map.get(type);
            if(list == null){
                list = new ArrayList();
                map.put(type, list);
            }
            list.add(o);
        }
        return map;
    }

    /**
     * Adding non-argument non-temrinal elements as well!
     * @param terminals Collection Terminal list containing non-argument methods
     * as well as conventional terminal primitives
     * @return HashMap the typed map
     */
    public HashMap makeTypedTerminals2(Collection augmentedTerminals){
        HashMap map = new HashMap();
        Class type = null;
        Iterator i = augmentedTerminals.iterator();
        while(i.hasNext()){
            Object o = i.next();
            if(o instanceof MethodCall){
                type = ((MethodCall)o).method.getReturnType();
                addType(map, type, o);
            }
            else if(o instanceof Function){
                type = ((Function)o).returnType;
                addType(map, type, o);
            }
            else {
                type = ((Expr)o).getType();
                addType(map, type, o);
            }
        }
        return map;
    }


    /**
     * Method that augments the typedTerminals HashMap with methods that take no parameters
     * @param typedTerms HashMap The typedTerminals Hash Map
     * @param functions Collection The collection of primitive methods
     */
    public void augmentTypedTerminals(HashMap typedTerms, Collection functions){
        Class[] parameterTypes = null;
        Class type = null;
        for(Iterator i = functions.iterator(); i.hasNext();){
          Object o = i.next();
          if(o instanceof MethodCall){
              parameterTypes = ((MethodCall)o).method.getParameterTypes();
              type = ((MethodCall)o).method.getReturnType();
              if(parameterTypes.length == 0){
                  addType(typedTerms, type, o);
              }
          }
          else if(o instanceof Function){
              parameterTypes = ((Function)o).parameterTypes;
              type = ((Function)o).returnType;
              if(parameterTypes.length == 0){
                  addType(typedTerms, type, o);
              }
          }
        }
    }

    /**
     * Method that augments the terminal set with methods that take no parameters
     * @param typedTerms HashMap The typedTerminals Hash Map
     * @param functions Collection The collection of primitive methods
     */
    public void augmentTerminals(Collection functions, Collection terminals){
        Class[] parameterTypes = null;
        for(Iterator i = functions.iterator(); i.hasNext();){
            Object o = i.next();
            if(o instanceof MethodCall){
                parameterTypes = ((MethodCall)o).method.getParameterTypes();
                if(parameterTypes.length == 0){
                    terminals.add(o);
                }
            }
            else if(o instanceof Function){
                parameterTypes = ((Function)o).parameterTypes;
                if(parameterTypes.length == 0){
                    terminals.add(o);
                }
            }
        }
    }

    public HashMap makePolymorphicMethods(Collection methods){
        return null;
    }

    ///////////////////////////////////////////////////////////
    /////////////HANDLING POLYMORPHIC PARAMETERS///////////////
    ///////////////////////////////////////////////////////////
    public HashMap makePolymorphicTerminals(Collection terminals){
        HashMap map = new HashMap();
        for(Iterator i = terminals.iterator(); i.hasNext();){
            Object o = i.next();
            Expr e = (Expr)o;
            Class type = e.getType();
            while(type != null){
                //add the current type
                addType(map, type, e);
                //add any implemented interfaces
                Class[] interfaces = type.getInterfaces();
                addInterfaces(map, interfaces, e);
                //do the primitive lookup
                Class primitive = (Class) wrappers.get(type);
                if(primitive != null){
                    addType(map, primitive, e);
                }
                //get the superclass
                type = type.getSuperclass();
            }
        }
        return map;
    }

    public void addInterfaces(HashMap map, Class[] interfaces, Object arg) {
        for (int i = 0; i < interfaces.length; i++) {
            //System.out.println("\t " + interfaces[i].getName());
            addType(map, interfaces[i], arg);
        }
    }

    public static void addType(HashMap map, Class type, Object arg) {
        ArrayList l = (ArrayList) map.get(type);
        if (l == null) {
            l = new ArrayList();
            map.put(type, l);
        }
        l.add(arg);
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * A type can appear in a particular depth by following this basic constraint:
     * The argument types of the function of the required type can be generated.
     * Regarding this restriction we observe that a non terminal element can be the root
     * of the tree of maximum depth i if and only if all its argument types can be generated
     * by trees of maximum depth i-1. To check this condition we use the type possibility table.
     * This map is keyed on the depth of the tree and holds the array list of all permissible
     * data types for the currest depth.
     * @param maxDepth The maximum depth allowed for a tree
     * @param functions The function set
     * @param terminals The terminal set
     * @return The type possibility table
     */
    public HashMap makeTypePossibilityTable(int maxDepth, Collection functions, Collection terminals){
        HashMap map = new HashMap();
        Iterator termIt = terminals.iterator();
        ArrayList listOne = new ArrayList();
        while(termIt.hasNext()){
            Class type = null;
            Object o = termIt.next();
            if(o instanceof MethodCall){
                type = ((MethodCall)o).method.getReturnType();
            }
            else if(o instanceof Function){
                type = ((Function)o).returnType;
            }
            else{
                type = ((Expr)o).getType();
            }
            if(!listOne.contains(type)){
                listOne.add(type);
            }
        }
        //add to the map with key being the tree depth
        //tree depth of zero means that this is a terminal node
        map.put(new Integer(0), listOne);
        for(int i=1; i<=maxDepth; i++){
            ArrayList typeList = new ArrayList();
            Iterator funcIt = functions.iterator();
            Class[] parameterTypes = {};
            Class returnType = null;
            while(funcIt.hasNext()){
                Object o = funcIt.next();
                if(o instanceof MethodCall){
                    MethodCall mthCall = (MethodCall)o;
                    parameterTypes = mthCall.method.getParameterTypes();
                    returnType = mthCall.method.getReturnType();
                }
                else if(o instanceof Function){
                    Function f = (Function)o;
                    parameterTypes = f.parameterTypes;
                    returnType = f.returnType;
                }
                else if(o instanceof If){
                    If ifStatement = (If)o;
                    parameterTypes = ifStatement.branchesTypes;
                    returnType = ifStatement.returnType;
                }
                if(isCallable(parameterTypes, map, i-1) && !typeList.contains(returnType)){
                    typeList.add(returnType);
                }
            }
            propagateTypes((ArrayList)map.get(new Integer(i-1)), typeList);
            map.put(new Integer(i), typeList);
        }
        return map;
    }

    //add all the types from table entry [i-1] to table entry [i]
    public void propagateTypes(ArrayList source, ArrayList target){
        for(Iterator i = source.iterator(); i.hasNext();){
            Class type = (Class)i.next();
            if(!target.contains(type)){
                target.add(type);
            }
        }
    }

    public void makeTypePossibilityTables(int maxDepth, Collection functions, Collection terminals){
        typePossibilityGrow = new HashMap();
        typePossibilityFull = new HashMap();
        //iterate through the terminal set first - get types for dzero depth
        Iterator termIt = terminals.iterator();
        ArrayList listOne = new ArrayList();
        while(termIt.hasNext()){
            Class type = null;
            Object o = termIt.next();
            if(o instanceof MethodCall){
                type = ((MethodCall)o).method.getReturnType();
            }
            else if(o instanceof Function){
                type = ((Function)o).returnType;
            }
            else{
                type = ((Expr)o).getType();
            }
            if(!listOne.contains(type)){
                listOne.add(type);
            }
        }
        //add to the map with key being the tree depth
        //tree depth of zero means that this is a terminal node
        typePossibilityGrow.put(new Integer(0), listOne);
        typePossibilityFull.put(new Integer(0), listOne);
        //now iterate through the non-terminal elements and add their types
        // - if they can be called, see comment above
        for(int i = 1; i <= maxDepth; i++){
            ArrayList typeListGrow = new ArrayList();
            ArrayList typeListFull = new ArrayList();
            Iterator funcIt = functions.iterator();
            Class[] parameterTypes = {};
            Class returnType = null;
            while(funcIt.hasNext()){
                Object o = funcIt.next();
                if(o instanceof MethodCall){
                    MethodCall mthCall = (MethodCall)o;
                    parameterTypes = mthCall.method.getParameterTypes();
                    returnType = mthCall.method.getReturnType();
                }
                else if(o instanceof Function){
                    Function f = (Function)o;
                    parameterTypes = f.parameterTypes;
                    returnType = f.returnType;
                }
                else if(o instanceof If){
                    If ifStatement = (If)o;
                    parameterTypes = ifStatement.branchesTypes;
                    returnType = ifStatement.returnType;
                }
                if(isCallable(parameterTypes, typePossibilityFull, i-1) && !typeListFull.contains(returnType)){
                    typeListFull.add(returnType);
                }
                if(isCallable(parameterTypes, typePossibilityGrow, i-1) && !typeListGrow.contains(returnType)){
                    typeListGrow.add(returnType);
                }
            }
            typePossibilityFull.put(new Integer(i), typeListFull);
            propagateTypes((ArrayList)typePossibilityGrow.get(new Integer(i-1)), typeListGrow);
            typePossibilityGrow.put(new Integer(i), typeListGrow);
        }
    }

    /**
     * To be considered callable the argument types of a root node should be generated.
     * @param types The array of types of the argument trees
     * @param map type possibility table
     * @param depth the required depth of the tree
     * @return true or false whether or not all the argument types exist for the required depth
     */
    public boolean isCallable(Class[] types, HashMap map, int depth){
        ArrayList typeList = (ArrayList)map.get(new Integer(depth));
        for(int i=0; i<types.length; i++){
            if(!typeList.contains(types[i])){
                //System.out.println("NOT Callable ["+(depth+1)+"] : "+sTypes(types)+" in type: "+types[i]);
                return false;
            }
        }
        //System.out.println("Callable : "+sTypes(types));
        return true;
    }

    public String sTypes(Class[] types){
        String s = "";
        for(int i=0; i<types.length; i++){
            s += types[i] + ",  ";
        }
        return s;
    }

    //for depth=1 to maxDepth
    public HashMap makeElementPossibilityTable(HashMap typePossibilities, int maxDepth, Collection functions){
        HashMap map = new HashMap();
        for(int i=1; i<=maxDepth; i++){
            ArrayList validFunctions = new ArrayList();
            Iterator it = functions.iterator();
            Class[] parameterTypes = {};
            while(it.hasNext()){
                Object o = it.next();
                if(o instanceof MethodCall){
                    MethodCall mthCall = (MethodCall)o;
                    //System.out.println("Considering: "+mthCall);
                    parameterTypes = mthCall.method.getParameterTypes();
                }
                else if(o instanceof Function){
                    Function f = (Function)o;
                    parameterTypes = f.parameterTypes;
                }
                else if(o instanceof If){
                    If ifStatement = (If)o;
                    parameterTypes = ifStatement.branchesTypes;
                }
                if(isCallable(parameterTypes, typePossibilities, i-1)){
                    validFunctions.add(o);
                }
                map.put(new Integer(i), validFunctions);
            }
        }
        return map;
    }

    public Collection getPossibleTypedElements(Collection typedFunctions, Collection possibleElements){
       ArrayList possibleTypedElements = new ArrayList();
       for (Iterator i = typedFunctions.iterator(); i.hasNext();){
          Object element = i.next();

          if(possibleElements.contains(element)){
             possibleTypedElements.add(element);
          }
       }
       return possibleTypedElements;
    }

    /**
     * This method is used to return the collection of function elements that are
     * of a particular type and of a particular arity
     * @param typedFunctions Collection Function elements of the appropriate type
     * @param arity int The arity of the required function
     * @return Collection Function elements satisfying the type and arity constraints
     */
    public Collection getTypedArity(Collection typedFunctions, int arity){
        ArrayList typedArity = new ArrayList();
        int noOfParams = 0;
        for(Iterator i = typedFunctions.iterator(); i.hasNext();){
            Object o = i.next();
            if(o instanceof MethodCall){
                MethodCall mc = (MethodCall)o;
                noOfParams = ((Class[])mc.method.getParameterTypes()).length;
            }
            else if(o instanceof Function){
                Function  f = (Function)o;
                noOfParams = ((Class[])f.parameterTypes).length;
            }
            else if(o instanceof If){
                If ifExpr = (If)o;
                noOfParams = ((Class[])ifExpr.branchesTypes).length;
            }

            if(noOfParams == arity){
                typedArity.add(o);
            }
        }
        return typedArity;
   }

   public Collection getTypedArity(Collection typedFunctions, Class[] paramTypes){
       ArrayList typedArity = new ArrayList();
       Class[] param_types = null;
       for(Iterator i = typedFunctions.iterator(); i.hasNext();){
            Object o = i.next();
            if(o instanceof MethodCall){
                MethodCall mc = (MethodCall)o;
                param_types = mc.method.getParameterTypes();
            }
            else if(o instanceof Function){
                Function  f = (Function)o;
                param_types = f.parameterTypes;
            }
            else if(o instanceof If){
                If ifExpr = (If)o;
                param_types = ifExpr.branchesTypes;
            }

            if(paramTypeMatching(paramTypes, param_types)){
                typedArity.add(o);
            }
        }
        return typedArity;
   }

   public boolean paramTypeMatching(Class[] paramTypes, Class[] paramTypesToCompare){
       if(paramTypes.length != paramTypesToCompare.length){
           return false;
       }
       for(int i=0; i<paramTypes.length; i++){
           if(paramTypes[i] != paramTypesToCompare[i]){
               return false;
           }
       }
       return true;
   }

   public HashMap makeMethodHolders(Collection functions){
       HashMap holders = new HashMap();
       for(Iterator i = functions.iterator(); i.hasNext();){
           Object o = i.next();
           if(o instanceof MethodCall){
               MethodCall mc = (MethodCall)o;
               String key = mc.method.getName();
               holders.put(key, mc);
           }
       }
       //System.out.println(holders);
       return holders;
   }


}
