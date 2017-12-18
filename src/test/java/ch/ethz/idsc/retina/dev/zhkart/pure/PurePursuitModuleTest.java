// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PurePursuitModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    purePursuitModule.runAlgo();
    purePursuitModule.last();
  }

  public void testSimple() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{0[m],0[m],0}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    assertFalse(purePursuitModule.purePursuitSteer.isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.isOperational());
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testClose() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    // assertTrue(purePursuitModule.purePursuitSteer.isOperational());
    // assertTrue(purePursuitModule.purePursuitRimo.isOperational());
    // Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // assertEquals(Quantity.of(-0.003134062491225534, "rad"), heading);
    // assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    // assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasible() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1+3.14}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    assertTrue(Scalars.isZero(heading));
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseOther() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1.2}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    // assertTrue(purePursuitModule.purePursuitSteer.isOperational());
    // assertTrue(purePursuitModule.purePursuitRimo.isOperational());
    // Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // assertEquals(Quantity.of(-0.17027499781304284, "rad"), heading);
    // assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    // assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEnd() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"));
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    // assertTrue(purePursuitModule.purePursuitSteer.isOperational());
    // assertTrue(purePursuitModule.purePursuitRimo.isOperational());
    // Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // assertEquals(Quantity.of(-0.10276854569090377, "rad"), heading);
    // assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    // assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  // public void testSpecific() throws Exception {
  // Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
  // Optional<Tensor> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
  // Tensor lookAhead = optional.get();
  // Scalar angle = ChassisGeometry.GLOBAL.steerAngleTowards(lookAhead);
  // assertTrue(Clip.function( //
  // Quantity.of(-0.015, "rad"), //
  // Quantity.of(-0.010, "rad")).isInside(angle));
  // }
  //
  // public void testSpecific2() throws Exception {
  // Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
  // Optional<Tensor> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
  // Tensor lookAhead = optional.get();
  // Scalar angle = ChassisGeometry.GLOBAL.steerAngleTowards(lookAhead);
  // assertTrue(Clip.function( //
  // Quantity.of(0.1, "rad"), //
  // Quantity.of(0.2, "rad")).isInside(angle));
  // }
  public void testPeriod() {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    double v1 = purePursuitModule.getPeriod();
    double v2 = PursuitConfig.GLOBAL.updatePeriodSeconds().number().doubleValue();
    assertEquals(v1, v2);
  }
}
