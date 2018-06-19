// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class AutoboxCompactComponentTest extends TestCase {
  public void testStartStop() {
    AutoboxCompactComponent autoboxCompactComponent = new AutoboxCompactComponent();
    autoboxCompactComponent.start();
    autoboxCompactComponent.stop();
  }
}
