// code by am
package ch.ethz.idsc.gokart.core.adas;

import junit.framework.TestCase;

public class NoFrictionModuleTest extends TestCase {
  public void testSimple() {
    NoFrictionExperiment noFriction = new NoFrictionExperiment();
    noFriction.first();
    assertFalse(noFriction.putEvent().isPresent());
    noFriction.last();
  }
}
