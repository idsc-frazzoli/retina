// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClientTest;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class PurePursuitModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    purePursuitModule.runAlgo();
    purePursuitModule.last();
  }

  public void testSome() {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    Scalar period = purePursuitModule.getPeriod();
    Clip clip = Clip.function(Quantity.of(0.01, "s"), Quantity.of(0.2, "s"));
    assertTrue(clip.isInside(period));
  }

  public void testSimple() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{0[m],0[m],0}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testClose() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.setCurve(DubendorfCurve.OVAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    JoystickLcmClientTest.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.private_isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // System.out.println(heading);
    // assertEquals(Quantity.of(-0.013455281968592674, "rad"), heading);
    Clip clip = Clip.function(Quantity.of(-0.02, "rad"), Quantity.of(-0.01, "rad"));
    clip.requireInside(heading);
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasible() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1+3.14}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    assertTrue(Scalars.isZero(heading));
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseInfeasibleInvalid() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1+1.14}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    assertTrue(Scalars.isZero(heading));
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseOther() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.setCurve(DubendorfCurve.OVAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{35.1[m], 44.9[m], 1.2}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    JoystickLcmClientTest.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.private_isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // System.out.println(heading);
    Clip clip = Clip.function(Quantity.of(-0.16, "rad"), Quantity.of(-0.12, "rad"));
    clip.requireInside(heading);
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEnd() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.setCurve(DubendorfCurve.OVAL);
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"), RealScalar.ONE);
    purePursuitModule.getEvent(gokartPoseEvent);
    JoystickLcmClientTest.publishAutonomous();
    purePursuitModule.runAlgo();
    assertTrue(purePursuitModule.purePursuitSteer.private_isOperational());
    assertTrue(purePursuitModule.purePursuitRimo.private_isOperational());
    Scalar heading = purePursuitModule.purePursuitSteer.getHeading();
    // System.out.println(heading);
    Clip clip = Clip.function(Quantity.of(-0.15, "rad"), Quantity.of(-0.10, "rad"));
    clip.requireInside(heading);
    assertFalse(purePursuitModule.purePursuitSteer.putEvent().isPresent());
    assertFalse(purePursuitModule.purePursuitRimo.putEvent().isPresent());
    purePursuitModule.last();
  }

  public void testCloseEndNoQuality() throws Exception {
    PurePursuitModule purePursuitModule = new PurePursuitModule();
    purePursuitModule.first();
    GokartPoseEvent gokartPoseEvent = //
        GokartPoseEvents.getPoseEvent(Tensors.fromString("{41.0[m], 37.4[m], -3.3}"), RealScalar.of(0.05));
    purePursuitModule.getEvent(gokartPoseEvent);
    JoystickLcmClientTest.publishAutonomous();
    purePursuitModule.runAlgo();
    assertFalse(purePursuitModule.purePursuitSteer.private_isOperational());
    assertFalse(purePursuitModule.purePursuitRimo.private_isOperational());
    purePursuitModule.last();
  }

  public void testSpecific() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 1}");
    Optional<Scalar> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
    Scalar lookAhead = optional.get();
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(lookAhead);
    assertTrue(Clip.function( //
        Quantity.of(-0.018, "rad"), //
        Quantity.of(-0.016, "rad")).isInside(angle));
  }

  public void testSpecific2() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 44.9[m], 0.9}");
    Optional<Scalar> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
    Scalar lookAhead = optional.get();
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(lookAhead);
    assertTrue(Clip.function( //
        Quantity.of(0.04, "rad"), //
        Quantity.of(0.07, "rad")).isInside(angle));
  }

  public void testLookAheadFail() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 42.9[m], 2.9}");
    Optional<Scalar> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
    assertFalse(optional.isPresent());
  }

  public void testLookAheadDistanceFail() throws Exception {
    Tensor pose = Tensors.fromString("{35.1[m], 420.9[m], 2.9}");
    Optional<Scalar> optional = PurePursuitModule.getLookAhead(pose, DubendorfCurve.OVAL);
    assertFalse(optional.isPresent());
  }
}
