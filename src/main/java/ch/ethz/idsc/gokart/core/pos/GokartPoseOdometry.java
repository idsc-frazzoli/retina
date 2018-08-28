// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.sys.SafetyCritical;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/** odometry is the integration of the wheels speeds to obtain the pose of the gokart
 * due to the high quality of the wheel/motor encoders the odometry turns out to be
 * quite smooth and stable.
 * 
 * <p>Naturally, any tire slip results in a loss of tracking accuracy. */
// TODO probably better to implement GokartPoseInterface instead of MappedPoseInterface -->
// no setPose() method is required.
// TODO add method that provides "delta_pose" for a variable dt
@SafetyCritical
public class GokartPoseOdometry implements MappedPoseInterface, RimoGetListener {
  private static final Scalar HALF = DoubleScalar.of(0.5);

  public static GokartPoseOdometry create(Tensor state) {
    return new GokartPoseOdometry(state);
  }

  public static GokartPoseOdometry create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }

  // ---
  private final Scalar dt = RimoSocket.INSTANCE.getGetPeriod(); // 1/250[s]
  private Tensor state;
  /** initial quality value == 0 */
  private Scalar quality = RealScalar.ZERO;

  private GokartPoseOdometry(Tensor state) {
    this.state = state.copy();
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    step(rimoGetEvent.getAngularRate_Y_pair());
  }

  /* package */ synchronized void step(Tensor angularRate_Y_pair) {
    // rad 0.14, ytir = 0.65 very good rotation tracking! but speed not accurate
    // rad 0.12, ytir = 0.54 good speed tracking, rotation ok
    Scalar radius = ChassisGeometry.GLOBAL.tireRadiusRear;
    // radius = Quantity.of(0.120, "m*rad^-1");
    Tensor speed_pair = angularRate_Y_pair.multiply(radius); // [rad*s^-1] * [m*rad^-1] == [m*s^-1]
    Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRear;
    // yTireRear = Quantity.of(0.54, "m");
    Flow flow = singleton(speed_pair.Get(0), speed_pair.Get(1), yTireRear);
    state = Se2CarIntegrator.INSTANCE.step(flow, state, dt);
  }

  /** .
   * @param speedL with unit "m*s^-1"
   * @param speedR with unit "m*s^-1"
   * @param yHalfWidth "m*rad^-1", hint: use ChassisGeometry.GLOBAL.yTireRear
   * @return */
  static Flow singleton(Scalar speedL, Scalar speedR, Scalar yHalfWidth) {
    Scalar speed = speedL.add(speedR).multiply(HALF);
    Scalar rate = speedR.subtract(speedL).multiply(HALF).divide(yHalfWidth);
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate)));
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return state.unmodifiable();
  }

  @Override
  public synchronized void setPose(Tensor pose, Scalar quality) {
    // TODO this is not good design: odometry should always be consistent integration of wheels!
    // other entities may track different poses
    state = pose.copy();
    this.quality = quality;
  }

  @Override
  public GokartPoseEvent getPoseEvent() {
    return GokartPoseEvents.getPoseEvent(state, quality);
  }
}
