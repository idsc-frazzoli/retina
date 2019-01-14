// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.AllGunsBlazing;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class CurvePurePursuitModuleTest extends TestCase {
  static void _checkFallback(Optional<SteerPutEvent> fallback) {
    SteerPutEvent steerPutEvent = fallback.get();
    assertEquals(steerPutEvent.getTorque(), Quantity.of(0, SteerPutEvent.UNIT_RTORQUE));
  }

  public void testFirstLast() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.first();
    purePursuitModule.runAlgo();
    purePursuitModule.last();
  }

  public void testChopUnit() {
    Scalar scalar = Chop.below(.1).apply(Quantity.of(.01, "rad*s^-1"));
    System.out.println(scalar);
  }

  public void testSome() {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    Scalar period = purePursuitModule.getPeriod();
    Clip clip = Clip.function(Quantity.of(0.01, "s"), Quantity.of(0.2, "s"));
    assertTrue(clip.isInside(period));
  }

  public void testSimple() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{0[m],0[m],0}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    _checkFallback(purePursuitModule.purePursuitSteer.putEvent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testSimple2() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.first();
    assertTrue(purePursuitModule.isForward());
    purePursuitModule.rimoGetListener.getEvent(RimoGetEvents.create(1000, 1000));
    assertTrue(purePursuitModule.isForward());
    purePursuitModule.rimoGetListener.getEvent(RimoGetEvents.create(-1000, -1000));
    assertFalse(purePursuitModule.isForward());
    purePursuitModule.rimoGetListener.getEvent(RimoGetEvents.create(-10, -10));
    assertTrue(purePursuitModule.isForward());
    purePursuitModule.last();
  }

  public void testClose() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.setCurve(Optional.of(DubendorfCurve.OVAL));
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.private_isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // System.out.println(heading);
    // assertEquals(Quantity.of(-0.013455281968592674, "rad"), heading);
    Clip clip = Clip.function(Quantity.of(-0.02, "rad"), Quantity.of(-0.01, "rad"));
    clip.requireInside(heading);
    _checkFallback(purePursuitModule.purePursuitSteer.putEvent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasible() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1+3.14}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    assertTrue(Scalars.isZero(heading));
    _checkFallback(purePursuitModule.purePursuitSteer.putEvent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasibleInvalid() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1+1.14}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    assertTrue(Scalars.isZero(heading));
    _checkFallback(purePursuitModule.purePursuitSteer.putEvent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseOther() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.setCurve(Optional.of(DubendorfCurve.OVAL));
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1.2}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.private_isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // System.out.println(heading);
    Clip clip = Clip.function(Quantity.of(-0.16, "rad"), Quantity.of(-0.12, "rad"));
    clip.requireInside(heading);
    _checkFallback(purePursuitModule.purePursuitSteer.putEvent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEnd() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.setCurve(Optional.of(DubendorfCurve.OVAL));
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.private_isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // System.out.println(heading);
    Clip clip = Clip.function(Quantity.of(-0.15, "rad"), Quantity.of(-0.10, "rad"));
    clip.requireInside(heading);
    _checkFallback(purePursuitModule.purePursuitSteer.putEvent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEndNoQuality() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"), RealScalar.of(0.05));
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    purePursuitModule.last();
  }
}
