// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2ForwardAction;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.math.Se2AxisYProject_Retina;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;

/** blocks rimo while anything is within a close distance in path */
public final class Urg04lxClearanceModule extends AbstractModule implements //
    LidarRayBlockListener, RimoPutProvider, RimoGetListener {
  public static final Scalar CLEARANCE = DoubleScalar.of(3.2); // distance in [m] from rear axle !!!
  private static final Scalar HALF = RealScalar.of(0.5);
  // ---
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private boolean isPathObstructed = true;
  // ---
  private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler(GokartLcmChannel.URG04LX_FRONT);
  private RimoGetEvent rimoGetEvent = null;

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(this);
    urg04lxLcmHandler.lidarAngularFiringCollector.addListener(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
    urg04lxLcmHandler.stopSubscriptions();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (Objects.nonNull(rimoGetEvent) && steerColumnInterface.isSteerColumnCalibrated()) {
      try {
        final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
        final int position = floatBuffer.position();
        // System.out.println("pos=" + position);
        int size = lidarRayBlockEvent.size();
        // System.out.println("sze=" + size);
        Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface); // <- calibration checked
        Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
        Clip clip = Clip.function(half.negate(), half);
        Tensor pair_unit = ChassisGeometry.GLOBAL.getDifferentialSpeed().pair(RealScalar.ONE, angle);
        Tensor pair_meas = rimoGetEvent.getAngularRate_Y_pair();
        // TODO formula needs analysis
        Scalar speed = RealScalar.ONE;
        // pair_meas.dot(pair_unit).Get().multiply(HALF);
        Tensor u = Tensors.of(speed, RealScalar.ZERO, angle.multiply(speed));
        Scalar min = DoubleScalar.POSITIVE_INFINITY;
        for (int index = 0; index < size; ++index) {
          float px = floatBuffer.get();
          float py = floatBuffer.get();
          // TODO magic const rear axle to lidarpos
          Tensor point = Tensors.vector(px + 1.64, py);
          Scalar t = Se2AxisYProject_Retina.of(u, point).negate();
          // System.out.println(t);
          Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t)));
          Tensor v = se2ForwardAction.apply(point);
          if (clip.isInside(v.Get(1)))
            min = Min.of(min, t.negate());
        }
        floatBuffer.position(position);
        isPathObstructed = Scalars.lessThan(min, CLEARANCE);
        // System.out.println("min = " + min + " " + isPathObstructed);
      } catch (Exception exception) {
        exception.printStackTrace();
        isPathObstructed = true;
      }
    } else
      isPathObstructed = true;
  }

  /***************************************************/
  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(isPathObstructed ? RimoPutEvent.PASSIVE : null);
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }
}
