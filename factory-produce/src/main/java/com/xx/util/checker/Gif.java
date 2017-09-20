package com.xx.util.checker;

public class Gif extends Checker
{
  public boolean check(String content)
  {
    return ("47494638".equals(content.substring(0, 8))) || 
      ("47494638".equals(content.substring(0, 8)));
  }
}