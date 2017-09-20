package com.xx.util.checker;

public class Swf extends Checker
{
  public boolean check(String content)
  {
    return ("435753".equals(content.substring(0, 6))) || 
      ("465753".equals(content.substring(0, 6)));
  }
}