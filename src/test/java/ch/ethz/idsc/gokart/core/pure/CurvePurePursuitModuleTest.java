// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.AllGunsBlazing;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class CurvePurePursuitModuleTest extends TestCase {
  public static void _checkFallback(Optional<SteerPutEvent> fallback) {
    SteerPutEvent steerPutEvent = fallback.get();
    assertEquals(steerPutEvent.getTorque(), Quantity.of(0, SteerPutEvent.UNIT_RTORQUE));
  }

  public void testFirstLast() throws Exception {
    CurvePursuitModule curvePurePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    curvePurePursuitModule.first();
    curvePurePursuitModule.runAlgo();
    curvePurePursuitModule.last();
  }

  public void testChopUnit() {
    Scalar scalar = Chop.below(.1).apply(Quantity.of(.01, SI.PER_SECOND));
    System.out.println(scalar);
  }

  public void testSome() {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    Scalar period = purePursuitModule.getPeriod();
    Clip clip = Clips.interval(Quantity.of(0.01, "s"), Quantity.of(0.2, "s"));
    assertTrue(clip.isInside(period));
  }

  public void testSimple() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{0[m], 0[m], 0}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    assertFalse(purePursuitModule.pursuitSteer.private_isOperational());
    // assertFalse(purePursuitModule.pursuitRimo.private_isOperational());
    _checkFallback(purePursuitModule.pursuitSteer.putEvent());
    // assertFalse(purePursuitModule.pursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testClose() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.setCurve(Optional.of(DubendorfCurve2.OVAL));
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{35.1[m], 44.9[m], 1}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.pursuitSteer.private_isOperational());
    // assertTrue(purePursuitModule.pursuitRimo.private_isOperational());
    Scalar ratio = purePursuitModule.pursuitSteer.getRatio();
    // System.out.println(heading);
    // assertEquals(Quantity.of(-0.013455281968592674, ""), heading);
    Clip clip = Clips.interval(Quantity.of(-0.02, SI.PER_METER), Quantity.of(-0.01, SI.PER_METER));
    clip.requireInside(ratio);
    _checkFallback(purePursuitModule.pursuitSteer.putEvent());
    // assertFalse(purePursuitModule.pursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasible() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{35.1[m], 44.9[m], 1+3.14}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.pursuitSteer.private_isOperational());
    // assertFalse(purePursuitModule.pursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.pursuitSteer.getRatio();
    assertTrue(Scalars.isZero(heading));
    _checkFallback(purePursuitModule.pursuitSteer.putEvent());
    // assertFalse(purePursuitModule.pursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasibleInvalid() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{35.1[m], 44.9[m], 1+1.14}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.pursuitSteer.private_isOperational());
    // assertFalse(purePursuitModule.pursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.pursuitSteer.getRatio();
    assertTrue(Scalars.isZero(heading));
    _checkFallback(purePursuitModule.pursuitSteer.putEvent());
    // assertFalse(purePursuitModule.pursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseOther() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.setCurve(Optional.of(DubendorfCurve2.OVAL));
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{35.1[m], 44.9[m], 1.2}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.pursuitSteer.private_isOperational());
    // assertTrue(purePursuitModule.pursuitRimo.private_isOperational());
    Scalar ratio = purePursuitModule.pursuitSteer.getRatio();
    // System.out.println(heading);
    Clip clip = Clips.interval(Quantity.of(-0.16, SI.PER_METER), Quantity.of(-0.12, SI.PER_METER));
    clip.requireInside(ratio);
    _checkFallback(purePursuitModule.pursuitSteer.putEvent());
    // assertFalse(purePursuitModule.pursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEnd() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.setCurve(Optional.of(DubendorfCurve2.OVAL));
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.pursuitSteer.private_isOperational());
    // assertTrue(purePursuitModule.pursuitRimo.private_isOperational());
    Scalar ratio = purePursuitModule.pursuitSteer.getRatio();
    // System.out.println(heading);
    Clip clip = Clips.interval(Quantity.of(-0.15, SI.PER_METER), Quantity.of(-0.10, SI.PER_METER));
    clip.requireInside(ratio);
    _checkFallback(purePursuitModule.pursuitSteer.putEvent());
    // assertFalse(purePursuitModule.pursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEndNoQuality() throws Exception {
    CurvePursuitModule purePursuitModule = new CurvePurePursuitModule(PurePursuitConfig.GLOBAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.offlineV1(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"), RealScalar.of(0.05));
    purePursuitModule.getEvent(gokartPoseEvent);
    AllGunsBlazing.publishAutonomous();
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.pursuitSteer.private_isOperational());
    // assertFalse(purePursuitModule.pursuitRimo.private_isOperational());
    purePursuitModule.last();
  }
}
