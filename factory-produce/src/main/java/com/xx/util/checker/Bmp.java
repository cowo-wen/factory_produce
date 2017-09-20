package com.xx.util.checker;

public class Bmp extends Checker
{
  public boolean check(String content)
  {
    return "424D".equalsIgnoreCase(content.substring(0, 4));
  }
}