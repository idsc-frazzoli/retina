// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.util.Arrays;

public class HelloWorld {
  public static void main(String[] args) {
    System.out.println("you're in.");
    Arrays.asList(args).forEach(System.out::println);
  }
}
