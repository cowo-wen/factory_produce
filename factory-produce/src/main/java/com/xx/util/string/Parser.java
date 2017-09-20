package com.xx.util.string;

import org.boris.expr.Expr;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.parser.ExprParser;
import org.boris.expr.util.Exprs;
import org.boris.expr.util.SimpleEvaluationContext;

public final class Parser
{
  public static final long parseLong(String expression)
  {
    String value = parse(expression);
    Double d = Double.valueOf(Double.parseDouble(value));
    return d.longValue();
  }

  public static final int parseInt(String expression)
  {
    String value = parse(expression);
    Double d = Double.valueOf(Double.parseDouble(value));
    return d.intValue();
  }

  public static final double parseDouble(String expression)
  {
    String value = parse(expression);
    Double d = Double.valueOf(Double.parseDouble(value));
    return d.doubleValue();
  }

  public static final float parseFloat(String expression)
  {
    String value = parse(expression);
    Double d = Double.valueOf(Double.parseDouble(value));
    return d.floatValue();
  }

  private static final String parse(String expression)
  {
    String value = "0";
    try
    {
      SimpleEvaluationContext context = new SimpleEvaluationContext();
      Expr e = ExprParser.parse(expression);
      Exprs.toUpperCase(e);

      if ((e instanceof ExprEvaluatable))
      {
        e = ((ExprEvaluatable)e).evaluate(context);
        value = e.toString();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return value;
  }

  public static void main(String[] args)
  {
    long l = parseLong("1+3*2-(5*6)/8");
    System.out.println(l);
  }
}