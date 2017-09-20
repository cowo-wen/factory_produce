package com.xx.util.checker;

public class Pdf extends Checker
{
  public boolean check(String content)
  {
    return "25504446".equals(content.substring(0, 8));
  }
}