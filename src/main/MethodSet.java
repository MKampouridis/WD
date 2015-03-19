package main;

public abstract interface MethodSet
{
  public static final MethodCall AND = new MethodCall(BooleanLogic.AND);
  public static final MethodCall OR = new MethodCall(BooleanLogic.OR);
  public static final MethodCall NAND = new MethodCall(BooleanLogic.NAND);
  public static final MethodCall NOR = new MethodCall(BooleanLogic.NOR);
  public static final MethodCall NOT = new MethodCall(BooleanLogic.NOT);
  public static final MethodCall ET = new MethodCall(Predicate.ET);
  public static final MethodCall GT = new MethodCall(Predicate.GT);
  public static final MethodCall GTOE = new MethodCall(Predicate.GTOE);
  public static final MethodCall LT = new MethodCall(Predicate.LT);
  public static final MethodCall LTOE = new MethodCall(Predicate.LTOE);
  public static final MethodCall ADD = new MethodCall(Arithmetic.ADD);
  public static final MethodCall SUB = new MethodCall(Arithmetic.SUB);
  public static final MethodCall MUL = new MethodCall(Arithmetic.MUL);
  public static final MethodCall DIV = new MethodCall(Arithmetic.DIV);
  public static final MethodCall EXP = new MethodCall(Arithmetic.EXP);
  public static final MethodCall LOG = new MethodCall(Arithmetic.LOG);
  public static final MethodCall SQRT = new MethodCall(Arithmetic.SQRT);
  public static final MethodCall POW = new MethodCall(Arithmetic.POW);
  public static final MethodCall MOD = new MethodCall(Arithmetic.MOD);
  public static final MethodCall SIN = new MethodCall(Arithmetic.SIN);
  public static final MethodCall COS = new MethodCall(Arithmetic.COS);
}
