// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEventSimulator;
import junit.framework.TestCase;

public class LinmotInitButtonTest extends TestCase {
  public void testEnabled() {
    LinmotInitButton lib = new LinmotInitButton();
    assertFalse(lib.isEnabled());
    lib.getEvent(LinmotGetEventSimulator.createNonOperational());
    assertTrue(lib.isEnabled());
    lib.getEvent(LinmotGetEventSimulator.createPos(23465, 23466));
    assertFalse(lib.isEnabled());
  }

  public void testStartStop() {
    LinmotInitButton lib = new LinmotInitButton();
    lib.start();
    lib.stop();
  }
}
