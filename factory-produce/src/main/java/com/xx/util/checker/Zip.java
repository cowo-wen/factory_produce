package com.xx.util.checker;

public class Zip extends Checker
{
  public boolean check(String content)
  {
    return "504B0304".equals(content.substring(0, 8));
  }
}