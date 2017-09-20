package com.xx.util.checker;

public class Jpg extends Checker
{
  public boolean check(String content)
  {
    return ("FFD8".equals(content.substring(0, 4))) || ("FFD8".equals(content.substring(0, 4)));
  }
}