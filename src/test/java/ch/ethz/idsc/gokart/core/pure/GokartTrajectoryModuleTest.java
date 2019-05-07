// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.dev.AllGunsBlazing;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnAdapter;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class GokartTrajectoryModuleTest extends TestCase {
  private static final ByteArrayConsumer BYTE_ARRAY_CONSUMER = new BinaryBlobPublisher(GokartLcmChannel.POSE_LIDAR);

  public void testSimple() throws Exception {
    GokartTrajectoryModule gokartTrajectoryModule = new GokartTrajectoryModule();
    gokartTrajectoryModule.first();
    gokartTrajectoryModule.last();
  }

  public void testPose() throws Exception {
    TrajectoryConfig trajectoryConfig = new TrajectoryConfig();
    trajectoryConfig.waypoints = "/dubilab/controlpoints/tires/20190116.csv";
    {
      PoseHelper.toUnitless(trajectoryConfig.getWaypoints().get(2));
    }
    GokartTrajectoryModule gokartTrajectoryModule = new GokartTrajectoryModule(trajectoryConfig);
    gokartTrajectoryModule.first();
    {
      BYTE_ARRAY_CONSUMER.accept( //
          GokartPoseEvents.offlineV1(Tensors.fromString("{36.8[m], 44.2[m], 0.8}"), RealScalar.ONE).asArray());
      RimoLcmServer.INSTANCE.getEvent( //
          RimoGetEvents.create(500, 500));
      Thread.sleep(50);
      gokartTrajectoryModule.runAlgo();
      Thread.sleep(500);
    }
    {
      Optional<Tensor> optional = gokartTrajectoryModule.curvePursuitModule.getCurve();
      assertTrue(optional.isPresent());
      Tensor curve = optional.get();
      List<Integer> dims = Dimensions.of(curve);
      assertEquals(dims.get(1), Integer.valueOf(3));
      assertTrue(15 < dims.get(0));
    }
    assertFalse(gokartTrajectoryModule.curvePursuitModule.purePursuitRimo.private_isOperational());
    assertFalse(gokartTrajectoryModule.curvePursuitModule.purePursuitSteer.private_isOperational());
    AllGunsBlazing.publishAutonomous();
    gokartTrajectoryModule.curvePursuitModule.runAlgo();
    assertTrue(gokartTrajectoryModule.curvePursuitModule.purePursuitRimo.private_isOperational());
    assertTrue(gokartTrajectoryModule.curvePursuitModule.purePursuitSteer.private_isOperational());
    {
      Optional<RimoPutEvent> optional = gokartTrajectoryModule.curvePursuitModule.purePursuitRimo.private_putEvent( //
          new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
      assertFalse(optional.isPresent());
    }
    {
      SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(0.3, "SCE"));
      assertTrue(steerColumnInterface.isSteerColumnCalibrated());
      assertTrue(gokartTrajectoryModule.curvePursuitModule.purePursuitRimo.private_isOperational());
      RimoGetEvent rge = RimoGetEvents.create(123, 234);
      gokartTrajectoryModule.curvePursuitModule.purePursuitRimo.rimoRateControllerWrap.getEvent(rge);
      Optional<RimoPutEvent> optional = //
          gokartTrajectoryModule.curvePursuitModule.purePursuitRimo.private_putEvent(steerColumnInterface);
      assertTrue(optional.isPresent());
    }
    {
      Optional<SteerPutEvent> optional = gokartTrajectoryModule.curvePursuitModule.purePursuitSteer.private_putEvent( //
          new SteerColumnAdapter(false, Quantity.of(0.3, "SCE")));
      CurvePurePursuitModuleTest._checkFallback(optional);
    }
    {
      SteerColumnInterface steerColumnInterface = new SteerColumnAdapter(true, Quantity.of(0.3, "SCE"));
      assertTrue(steerColumnInterface.isSteerColumnCalibrated());
      assertTrue(gokartTrajectoryModule.curvePursuitModule.purePursuitSteer.private_isOperational());
      Optional<SteerPutEvent> optional = //
          gokartTrajectoryModule.curvePursuitModule.purePursuitSteer.private_putEvent(steerColumnInterface);
      assertTrue(optional.isPresent());
    }
    {
      RimoLcmServer.INSTANCE.getEvent( //
          RimoGetEvents.create(-900, -900));
      BYTE_ARRAY_CONSUMER.accept( //
          GokartPoseEvents.offlineV1(Tensors.fromString("{31.8[m], 38.2[m], 0.8}"), RealScalar.ONE).asArray());
      Thread.sleep(1000);
      gokartTrajectoryModule.runAlgo();
      Optional<Tensor> optional = gokartTrajectoryModule.curvePursuitModule.getCurve();
      assertTrue(optional.isPresent());
    }
    gokartTrajectoryModule.last();
  }

  public void testFlows() {
    GokartTrajectoryModule gokartTrajectoryModule = new GokartTrajectoryModule();
    Collection<Flow> collection = gokartTrajectoryModule.getFlows(4);
    assertEquals(collection.size(), 5);
    for (Flow flow : collection) {
      Tensor u = flow.getU();
      Sign.requirePositive(u.Get(0));
      assertTrue(Scalars.isZero(u.Get(1)));
    }
  }
}
