// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetHelper;
import junit.framework.TestCase;

public class LinmotTakeoverModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    linmotTakeoverModule.first();
    linmotTakeoverModule.last();
  }

  public void testSimple() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    assertFalse(linmotTakeoverModule.putEvent().isPresent());
    Thread.sleep(60);
    assertTrue(linmotTakeoverModule.putEvent().isPresent());
  }

  public void testDiscrepancyFine() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-50_000, -50_000));
    Thread.sleep(20);
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-50_000, -50_000));
    Thread.sleep(20);
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-50_000, -50_000));
    Thread.sleep(20);
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-50_000, -50_000));
    assertFalse(linmotTakeoverModule.putEvent().isPresent());
  }

  public void testDiscrepancyBad() throws Exception {
    LinmotTakeoverModule linmotTakeoverModule = new LinmotTakeoverModule();
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-100_000, -50_000));
    Thread.sleep(20);
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-100_000, -50_000));
    Thread.sleep(20);
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-100_000, -50_000));
    Thread.sleep(20);
    linmotTakeoverModule.getEvent(LinmotGetHelper.createPos(-100_000, -50_000));
    assertTrue(linmotTakeoverModule.putEvent().isPresent());
  }
}
