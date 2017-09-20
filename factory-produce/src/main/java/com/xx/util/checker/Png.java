package com.xx.util.checker;

public class Png extends Checker
{
  public boolean check(String content)
  {
    return "89504E470D0A".equals(content.substring(0, 12));
  }
}