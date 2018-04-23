// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class AutoboxCompactComponentTest extends TestCase {
  public void testStartStop() {
    AutoboxCompactComponent acc = new AutoboxCompactComponent();
    acc.start();
    acc.stop();
  }
}
