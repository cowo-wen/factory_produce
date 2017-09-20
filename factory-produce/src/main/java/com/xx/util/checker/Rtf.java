package com.xx.util.checker;

public class Rtf extends Checker
{
  public boolean check(String content)
  {
    return "7B5C727466".equals(content.substring(0, 10));
  }
}