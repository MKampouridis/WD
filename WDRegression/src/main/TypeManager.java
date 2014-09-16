package main;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TypeManager
  implements MethodSet
{
  HashMap<Class<?>, ArrayList<Object>> typedFunctions;
  HashMap typedTerminals;
  HashMap elementPossibility;
  static HashMap methodHolders;
  HashMap typePossibilityGrow;
  HashMap typePossibilityFull;
  HashMap elementPossibilityGrow;
  HashMap elementPossibilityFull;
  static Function evolvedMethod = new Function(Double.TYPE, new Class[] { Double.TYPE });
  static HashMap wrappers = new HashMap();
  
  static
  {
    wrappers.put(Integer.class, Integer.TYPE);
    wrappers.put(Double.class, Double.TYPE);
  }
  
  public TypeManager(ArrayList functions, ArrayList terminals, int maxDepth)
  {
    this.typedFunctions = makeTypedFunctions(functions);
    this.typedTerminals = makeTypedTerminals(terminals);
    augmentTypedTerminals(this.typedTerminals, functions);
    augmentTerminals(functions, terminals);
    this.elementPossibility = makeElementPossibilityTable(makeTypePossibilityTable(maxDepth, functions, terminals), maxDepth, functions);
    
    methodHolders = makeMethodHolders(functions);
  }
  
  public TypeManager(int maxDepth, ArrayList functions, ArrayList terminals)
  {
    this.typedFunctions = makeTypedFunctions(functions);
    augmentTerminals(functions, terminals);
    this.typedTerminals = makeTypedTerminals2(terminals);
    makeTypePossibilityTables(maxDepth, functions, terminals);
    this.elementPossibilityGrow = makeElementPossibilityTable(this.typePossibilityGrow, maxDepth, functions);
    System.out.println("Element Possiblity Grow:");
    System.out.println(this.elementPossibilityGrow);
    this.elementPossibilityFull = makeElementPossibilityTable(this.typePossibilityFull, maxDepth, functions);
    System.out.println("Element Possiblity Full:");
    System.out.println(this.elementPossibilityFull);
  }
  
  public TypeManager(Collection functions, Collection terminals)
  {
    this.typedFunctions = makeTypedFunctions(functions);
    this.typedTerminals = makeTypedTerminals(terminals);
    methodHolders = makeMethodHolders(functions);
  }
  
  public static void main(String[] args)
  {
    ArrayList funcs = new ArrayList();
    funcs.add(ADD);
    funcs.add(SUB);
    funcs.add(evolvedMethod);
    funcs.add(new If(Double.TYPE, new Class[] { Boolean.TYPE, Double.TYPE, Double.TYPE }));
    funcs.add(MUL);
    funcs.add(DIV);
    

    ArrayList terms = new ArrayList();
    
    terms.add(new Constant(new Double(40.0D), Double.class));
    




    TypeManager man = new TypeManager();
    HashMap m = man.makePolymorphicTerminals(terms);
    System.out.println(m);
  }
  
  public HashMap makeTypedFunctions(Collection functions)
  {
    HashMap map = new HashMap();
    Class returnType = null;
    Iterator i = functions.iterator();
    while (i.hasNext())
    {
      Object o = i.next();
      if ((o instanceof MethodCall)) {
        returnType = ((MethodCall)o).method.getReturnType();
      } else if ((o instanceof Function)) {
        returnType = ((Function)o).returnType;
      } else if ((o instanceof If)) {
        returnType = ((If)o).returnType;
      }
      ArrayList list = (ArrayList)map.get(returnType);
      if (list == null)
      {
        list = new ArrayList();
        map.put(returnType, list);
      }
      list.add(o);
    }
    return map;
  }
  
  public HashMap makeTypedTerminals(Collection terminals)
  {
    HashMap map = new HashMap();
    Class type = null;
    Iterator i = terminals.iterator();
    while (i.hasNext())
    {
      Object o = i.next();
      Expr e = (Expr)o;
      type = e.getType();
      ArrayList list = (ArrayList)map.get(type);
      if (list == null)
      {
        list = new ArrayList();
        map.put(type, list);
      }
      list.add(o);
    }
    return map;
  }
  
  public HashMap makeTypedTerminals2(Collection augmentedTerminals)
  {
    HashMap map = new HashMap();
    Class type = null;
    Iterator i = augmentedTerminals.iterator();
    while (i.hasNext())
    {
      Object o = i.next();
      if ((o instanceof MethodCall))
      {
        type = ((MethodCall)o).method.getReturnType();
        addType(map, type, o);
      }
      else if ((o instanceof Function))
      {
        type = ((Function)o).returnType;
        addType(map, type, o);
      }
      else
      {
        type = ((Expr)o).getType();
        addType(map, type, o);
      }
    }
    return map;
  }
  
  public void augmentTypedTerminals(HashMap typedTerms, Collection functions)
  {
    Class[] parameterTypes = null;
    Class type = null;
    for (Iterator i = functions.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if ((o instanceof MethodCall))
      {
        parameterTypes = ((MethodCall)o).method.getParameterTypes();
        type = ((MethodCall)o).method.getReturnType();
        if (parameterTypes.length == 0) {
          addType(typedTerms, type, o);
        }
      }
      else if ((o instanceof Function))
      {
        parameterTypes = ((Function)o).parameterTypes;
        type = ((Function)o).returnType;
        if (parameterTypes.length == 0) {
          addType(typedTerms, type, o);
        }
      }
    }
  }
  
  public void augmentTerminals(Collection functions, Collection terminals)
  {
    Class[] parameterTypes = null;
    for (Iterator i = functions.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if ((o instanceof MethodCall))
      {
        parameterTypes = ((MethodCall)o).method.getParameterTypes();
        if (parameterTypes.length == 0) {
          terminals.add(o);
        }
      }
      else if ((o instanceof Function))
      {
        parameterTypes = ((Function)o).parameterTypes;
        if (parameterTypes.length == 0) {
          terminals.add(o);
        }
      }
    }
  }
  
  public HashMap makePolymorphicMethods(Collection methods)
  {
    return null;
  }
  
  public HashMap makePolymorphicTerminals(Collection terminals)
  {
    HashMap map = new HashMap();
    for (Iterator i = terminals.iterator(); i.hasNext();)
    {
      Object o = i.next();
      Expr e = (Expr)o;
      Class type = e.getType();
      while (type != null)
      {
        addType(map, type, e);
        
        Class[] interfaces = type.getInterfaces();
        addInterfaces(map, interfaces, e);
        
        Class primitive = (Class)wrappers.get(type);
        if (primitive != null) {
          addType(map, primitive, e);
        }
        type = type.getSuperclass();
      }
    }
    return map;
  }
  
  public void addInterfaces(HashMap map, Class[] interfaces, Object arg)
  {
    for (int i = 0; i < interfaces.length; i++) {
      addType(map, interfaces[i], arg);
    }
  }
  
  public static void addType(HashMap map, Class type, Object arg)
  {
    ArrayList l = (ArrayList)map.get(type);
    if (l == null)
    {
      l = new ArrayList();
      map.put(type, l);
    }
    l.add(arg);
  }
  
  public HashMap makeTypePossibilityTable(int maxDepth, Collection functions, Collection terminals)
  {
    HashMap map = new HashMap();
    Iterator termIt = terminals.iterator();
    ArrayList listOne = new ArrayList();
    while (termIt.hasNext())
    {
      Class type = null;
      Object o = termIt.next();
      if ((o instanceof MethodCall)) {
        type = ((MethodCall)o).method.getReturnType();
      } else if ((o instanceof Function)) {
        type = ((Function)o).returnType;
      } else {
        type = ((Expr)o).getType();
      }
      if (!listOne.contains(type)) {
        listOne.add(type);
      }
    }
    map.put(new Integer(0), listOne);
    for (int i = 1; i <= maxDepth; i++)
    {
      ArrayList typeList = new ArrayList();
      Iterator funcIt = functions.iterator();
      Class[] parameterTypes = new Class[0];
      Class returnType = null;
      while (funcIt.hasNext())
      {
        Object o = funcIt.next();
        if ((o instanceof MethodCall))
        {
          MethodCall mthCall = (MethodCall)o;
          parameterTypes = mthCall.method.getParameterTypes();
          returnType = mthCall.method.getReturnType();
        }
        else if ((o instanceof Function))
        {
          Function f = (Function)o;
          parameterTypes = f.parameterTypes;
          returnType = f.returnType;
        }
        else if ((o instanceof If))
        {
          If ifStatement = (If)o;
          parameterTypes = ifStatement.branchesTypes;
          returnType = ifStatement.returnType;
        }
        if ((isCallable(parameterTypes, map, i - 1)) && (!typeList.contains(returnType))) {
          typeList.add(returnType);
        }
      }
      propagateTypes((ArrayList)map.get(new Integer(i - 1)), typeList);
      map.put(new Integer(i), typeList);
    }
    return map;
  }
  
  public void propagateTypes(ArrayList source, ArrayList target)
  {
    for (Iterator i = source.iterator(); i.hasNext();)
    {
      Class type = (Class)i.next();
      if (!target.contains(type)) {
        target.add(type);
      }
    }
  }
  
  public void makeTypePossibilityTables(int maxDepth, Collection functions, Collection terminals)
  {
    this.typePossibilityGrow = new HashMap();
    this.typePossibilityFull = new HashMap();
    
    Iterator termIt = terminals.iterator();
    ArrayList listOne = new ArrayList();
    while (termIt.hasNext())
    {
      Class type = null;
      Object o = termIt.next();
      if ((o instanceof MethodCall)) {
        type = ((MethodCall)o).method.getReturnType();
      } else if ((o instanceof Function)) {
        type = ((Function)o).returnType;
      } else {
        type = ((Expr)o).getType();
      }
      if (!listOne.contains(type)) {
        listOne.add(type);
      }
    }
    this.typePossibilityGrow.put(new Integer(0), listOne);
    this.typePossibilityFull.put(new Integer(0), listOne);
    for (int i = 1; i <= maxDepth; i++)
    {
      ArrayList typeListGrow = new ArrayList();
      ArrayList typeListFull = new ArrayList();
      Iterator funcIt = functions.iterator();
      Class[] parameterTypes = new Class[0];
      Class returnType = null;
      while (funcIt.hasNext())
      {
        Object o = funcIt.next();
        if ((o instanceof MethodCall))
        {
          MethodCall mthCall = (MethodCall)o;
          parameterTypes = mthCall.method.getParameterTypes();
          returnType = mthCall.method.getReturnType();
        }
        else if ((o instanceof Function))
        {
          Function f = (Function)o;
          parameterTypes = f.parameterTypes;
          returnType = f.returnType;
        }
        else if ((o instanceof If))
        {
          If ifStatement = (If)o;
          parameterTypes = ifStatement.branchesTypes;
          returnType = ifStatement.returnType;
        }
        if ((isCallable(parameterTypes, this.typePossibilityFull, i - 1)) && (!typeListFull.contains(returnType))) {
          typeListFull.add(returnType);
        }
        if ((isCallable(parameterTypes, this.typePossibilityGrow, i - 1)) && (!typeListGrow.contains(returnType))) {
          typeListGrow.add(returnType);
        }
      }
      this.typePossibilityFull.put(new Integer(i), typeListFull);
      propagateTypes((ArrayList)this.typePossibilityGrow.get(new Integer(i - 1)), typeListGrow);
      this.typePossibilityGrow.put(new Integer(i), typeListGrow);
    }
  }
  
  public boolean isCallable(Class[] types, HashMap map, int depth)
  {
    ArrayList typeList = (ArrayList)map.get(new Integer(depth));
    for (int i = 0; i < types.length; i++) {
      if (!typeList.contains(types[i])) {
        return false;
      }
    }
    return true;
  }
  
  public String sTypes(Class[] types)
  {
    String s = "";
    for (int i = 0; i < types.length; i++) {
      s = s + types[i] + ",  ";
    }
    return s;
  }
  
  public HashMap makeElementPossibilityTable(HashMap typePossibilities, int maxDepth, Collection functions)
  {
    HashMap map = new HashMap();
    for (int i = 1; i <= maxDepth; i++)
    {
      ArrayList validFunctions = new ArrayList();
      Iterator it = functions.iterator();
      Class[] parameterTypes = new Class[0];
      while (it.hasNext())
      {
        Object o = it.next();
        if ((o instanceof MethodCall))
        {
          MethodCall mthCall = (MethodCall)o;
          
          parameterTypes = mthCall.method.getParameterTypes();
        }
        else if ((o instanceof Function))
        {
          Function f = (Function)o;
          parameterTypes = f.parameterTypes;
        }
        else if ((o instanceof If))
        {
          If ifStatement = (If)o;
          parameterTypes = ifStatement.branchesTypes;
        }
        if (isCallable(parameterTypes, typePossibilities, i - 1)) {
          validFunctions.add(o);
        }
        map.put(new Integer(i), validFunctions);
      }
    }
    return map;
  }
  
  public Collection getPossibleTypedElements(Collection typedFunctions, Collection possibleElements)
  {
    ArrayList possibleTypedElements = new ArrayList();
    for (Iterator i = typedFunctions.iterator(); i.hasNext();)
    {
      Object element = i.next();
      if (possibleElements.contains(element)) {
        possibleTypedElements.add(element);
      }
    }
    return possibleTypedElements;
  }
  
  public Collection getTypedArity(Collection typedFunctions, int arity)
  {
    ArrayList typedArity = new ArrayList();
    int noOfParams = 0;
    for (Iterator i = typedFunctions.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if ((o instanceof MethodCall))
      {
        MethodCall mc = (MethodCall)o;
        noOfParams = ((Class[])mc.method.getParameterTypes()).length;
      }
      else if ((o instanceof Function))
      {
        Function f = (Function)o;
        noOfParams = ((Class[])f.parameterTypes).length;
      }
      else if ((o instanceof If))
      {
        If ifExpr = (If)o;
        noOfParams = ((Class[])ifExpr.branchesTypes).length;
      }
      if (noOfParams == arity) {
        typedArity.add(o);
      }
    }
    return typedArity;
  }
  
  public Collection getTypedArity(Collection typedFunctions, Class[] paramTypes)
  {
    ArrayList typedArity = new ArrayList();
    Class[] param_types = null;
    for (Iterator i = typedFunctions.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if ((o instanceof MethodCall))
      {
        MethodCall mc = (MethodCall)o;
        param_types = mc.method.getParameterTypes();
      }
      else if ((o instanceof Function))
      {
        Function f = (Function)o;
        param_types = f.parameterTypes;
      }
      else if ((o instanceof If))
      {
        If ifExpr = (If)o;
        param_types = ifExpr.branchesTypes;
      }
      if (paramTypeMatching(paramTypes, param_types)) {
        typedArity.add(o);
      }
    }
    return typedArity;
  }
  
  public boolean paramTypeMatching(Class[] paramTypes, Class[] paramTypesToCompare)
  {
    if (paramTypes.length != paramTypesToCompare.length) {
      return false;
    }
    for (int i = 0; i < paramTypes.length; i++) {
      if (paramTypes[i] != paramTypesToCompare[i]) {
        return false;
      }
    }
    return true;
  }
  
  public HashMap makeMethodHolders(Collection functions)
  {
    HashMap holders = new HashMap();
    for (Iterator i = functions.iterator(); i.hasNext();)
    {
      Object o = i.next();
      if ((o instanceof MethodCall))
      {
        MethodCall mc = (MethodCall)o;
        String key = mc.method.getName();
        holders.put(key, mc);
      }
    }
    return holders;
  }
  
  public TypeManager() {}
}
