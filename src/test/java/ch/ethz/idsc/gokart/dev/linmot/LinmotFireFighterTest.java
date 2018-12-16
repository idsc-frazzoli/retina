// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import junit.framework.TestCase;

public class LinmotFireFighterTest extends TestCase {
  public void testSimple() {
    LinmotFireFighter lff = LinmotFireFighter.INSTANCE;
    lff.getEvent(LinmotGetHelper.createTemperature(700, 700));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(1000, 700));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(1200, 700));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(1000, 700));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(700, 700));
    assertFalse(lff.putEvent().isPresent());
  }

  public void testSimple2() {
    LinmotFireFighter lff = LinmotFireFighter.INSTANCE;
    lff.getEvent(LinmotGetHelper.createTemperature(700, 700));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(700, 1000));
    assertFalse(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(700, 1200));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(700, 1000));
    assertTrue(lff.putEvent().isPresent());
    lff.getEvent(LinmotGetHelper.createTemperature(700, 700));
    assertFalse(lff.putEvent().isPresent());
  }
}
