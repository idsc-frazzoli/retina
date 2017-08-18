// code by jph
package ch.ethz.idsc.retina.demo.jph;

import lcm.spy.Spy;

enum StandaloneSpy {
  ;
  public static void main(String[] args) throws Exception {
    new Spy(""); // non-blocking
    System.out.println("here...");
  }
}
