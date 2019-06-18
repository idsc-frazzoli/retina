// code by jph
package ch.ethz.idsc.gokart.core.adas;

import junit.framework.TestCase;

public class PerSectionModuleTest extends TestCase {
  public void testSimple() {
    SpeedLimitPerSectionModule perSectionModule = new SpeedLimitPerSectionModule();
    perSectionModule.first();
    assertFalse(perSectionModule.putEvent().isPresent());
    perSectionModule.last();
  }

  public void testSimple1() {
    SpeedLimitPerSectionModule perSectionModule = new SpeedLimitPerSectionModule();
    perSectionModule.first();
    perSectionModule.putEvent();
    perSectionModule.last();
  }

  public void testSimple3() {
    SpeedLimitPerSectionModule perSectionModule = new SpeedLimitPerSectionModule();
    perSectionModule.first();
    perSectionModule.getEvent(null);
    perSectionModule.last();
  }
}
