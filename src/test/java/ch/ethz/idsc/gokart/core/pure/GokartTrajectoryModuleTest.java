// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClientTest;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class GokartTrajectoryModuleTest extends TestCase {
  public void testSimple() throws Exception {
    GokartTrajectoryModule gtm = new GokartTrajectoryModule();
    gtm.first();
    assertTrue(Objects.nonNull(gtm.waypoints));
    gtm.last();
  }

  public void testPose() throws Exception {
    GokartTrajectoryModule gtm = new GokartTrajectoryModule();
    gtm.first();
    {
      GokartPoseLcmServer.INSTANCE.publish( //
          GokartPoseEvents.getPoseEvent(Tensors.fromString("{36.8[m], 44.2[m], 0.8}"), RealScalar.ONE));
      RimoLcmServer.INSTANCE.getEvent( //
          RimoGetEvents.create(500, 500));
      Thread.sleep(50);
      gtm.runAlgo();
      Thread.sleep(500);
    }
    {
      Optional<Tensor> optional = gtm.purePursuitModule.getCurve();
      assertTrue(optional.isPresent());
      Tensor curve = optional.get();
      List<Integer> dims = Dimensions.of(curve);
      assertEquals(dims.get(1), Integer.valueOf(2));
      assertTrue(15 < dims.get(0));
    }
    assertFalse(gtm.purePursuitModule.purePursuitRimo.private_isOperational());
    assertFalse(gtm.purePursuitModule.purePursuitSteer.private_isOperational());
    JoystickLcmClientTest.publishAutonomous();
    gtm.purePursuitModule.runAlgo();
    assertTrue(gtm.purePursuitModule.purePursuitRimo.private_isOperational());
    assertTrue(gtm.purePursuitModule.purePursuitSteer.private_isOperational());
    {
      Optional<RimoPutEvent> optional = gtm.purePursuitModule.purePursuitRimo.private_putEvent( //
          new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent());
    }
    {
      SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(0.3, "SCE"));
      assertTrue(steerColumnInterface.isSteerColumnCalibrated());
      assertTrue(gtm.purePursuitModule.purePursuitRimo.private_isOperational());
      RimoGetEvent rge = RimoGetEvents.create(123, 234);
      gtm.purePursuitModule.purePursuitRimo.rimoRateControllerWrap.getEvent(rge);
      Optional<RimoPutEvent> optional = //
          gtm.purePursuitModule.purePursuitRimo.private_putEvent(steerColumnInterface);
      assertTrue(optional.isPresent());
    }
    {
      Optional<SteerPutEvent> optional = gtm.purePursuitModule.purePursuitSteer.private_putEvent( //
          new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
      CurvePurePursuitModuleTest._checkFallback(optional);
    }
    {
      SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(0.3, "SCE"));
      assertTrue(steerColumnInterface.isSteerColumnCalibrated());
      assertTrue(gtm.purePursuitModule.purePursuitSteer.private_isOperational());
      Optional<SteerPutEvent> optional = //
          gtm.purePursuitModule.purePursuitSteer.private_putEvent(steerColumnInterface);
      assertTrue(optional.isPresent());
    }
    {
      RimoLcmServer.INSTANCE.getEvent( //
          RimoGetEvents.create(-900, -900));
      GokartPoseLcmServer.INSTANCE.publish( //
          GokartPoseEvents.getPoseEvent(Tensors.fromString("{31.8[m], 38.2[m], 0.8}"), RealScalar.ONE));
      Thread.sleep(1000);
      gtm.runAlgo();
      Optional<Tensor> optional = gtm.purePursuitModule.getCurve();
      assertTrue(optional.isPresent());
    }
    gtm.last();
  }

  public void testFlows() {
    GokartTrajectoryModule gokartTrajectoryModule = new GokartTrajectoryModule();
    Collection<Flow> collection = gokartTrajectoryModule.flowsInterface.getFlows(4);
    assertEquals(collection.size(), 5);
    for (Flow flow : collection) {
      Tensor u = flow.getU();
      Sign.requirePositive(u.Get(0));
      assertTrue(Scalars.isZero(u.Get(1)));
    }
  }
}
