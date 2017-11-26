// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.Se2AxisYProject;
import ch.ethz.idsc.owl.math.map.Se2ForwardAction;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;

/** blocks rimo while anything is within a close distance in path */
public final class Urg04lxClearanceModule extends AbstractModule implements LidarRayBlockListener, RimoPutProvider {
  public static final Scalar CLEARANCE = DoubleScalar.of(3.0); // distance in [m] from rear axle !!!
  // ---
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  private boolean isPathObstructed = true;
  // ---
  private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler(GokartLcmChannel.URG04LX_FRONT);

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
    urg04lxLcmHandler.lidarAngularFiringCollector.addListener(this);
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    urg04lxLcmHandler.stopSubscriptions();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteeringCalibrated()) {
      final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      final int position = floatBuffer.position();
      int size = lidarRayBlockEvent.size();
      Scalar angle = gokartStatusEvent.getSteeringAngle();
      Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
      Clip clip = Clip.function(half.negate(), half);
      Tensor u = Tensors.of(RealScalar.ONE, RealScalar.ZERO, angle); // TODO replace by actual speed
      Scalar min = DoubleScalar.POSITIVE_INFINITY;
      for (int index = 0; index < size; ++index) {
        // TODO magic const rear axle to lidarpos
        Tensor point = Tensors.vector(floatBuffer.get() + 1.64, floatBuffer.get());
        Scalar t = Se2AxisYProject.of(u, point).negate();
        Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t)));
        Tensor v = se2ForwardAction.apply(point);
        if (clip.isInside(v.Get(1)))
          min = Min.of(min, t.negate());
      }
      floatBuffer.position(position);
      isPathObstructed = Scalars.lessThan(min, CLEARANCE);
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
}
