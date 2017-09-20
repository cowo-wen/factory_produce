package com.xx.util.checker;

public class Rar extends Checker
{
  public boolean check(String content)
  {
    return "52617221".equals(content.substring(0, 8));
  }
}