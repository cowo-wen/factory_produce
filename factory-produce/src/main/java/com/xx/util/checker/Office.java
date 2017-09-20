package com.xx.util.checker;

public class Office extends Checker
{
  public boolean check(String content)
  {
    return ("D0CF11E0A1B11AE1".equals(content.substring(0, 16))) || 
      ("504B030414000600".equals(content.substring(0, 16)));
  }
}