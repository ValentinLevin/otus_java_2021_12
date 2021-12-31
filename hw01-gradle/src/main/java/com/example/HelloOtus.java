package com.example;

import com.google.common.base.Strings;

public class HelloOtus {
  public static void main(String[] args) {
    int startSpaceCount = 0;
    for (String word : "Happy New Year 2022!".split(" ")) {
      System.out.println(Strings.repeat(" ", startSpaceCount) + word);
      startSpaceCount += word.length() + 1;
    }
  }
}