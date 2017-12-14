// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import junit.framework.TestCase;

public class LinmotFireFighterTest extends TestCase {
  public void testSimple() {
    LinmotFireFighter lff = LinmotFireFighter.INSTANCE;
    lff.getEvent(LinmotGetEventSimulator.create(700, 700));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(1000, 700));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(1200, 700));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(1000, 700));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(700, 700));
    assertFalse(lff.putEvent().isPresent());
  }

  public void testSimple2() {
    LinmotFireFighter lff = LinmotFireFighter.INSTANCE;
    lff.getEvent(LinmotGetEventSimulator.create(700, 700));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(700, 1000));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(700, 1200));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(700, 1000));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetEventSimulator.create(700, 700));
    assertFalse(lff.putEvent().isPresent());
  }
}
