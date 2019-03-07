// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** odometry is the integration of the wheels speeds to obtain the pose of the gokart
 * due to the high quality of the wheel/motor encoders the odometry turns out to be
 * quite smooth and stable.
 * 
 * <p>Naturally, any tire slip results in a loss of tracking accuracy. */
// TODO probably better to implement GokartPoseInterface instead of MappedPoseInterface -->
// no setPose() method is required.
// TODO add method that provides "delta_pose" for a variable dt
public abstract class GokartPoseOdometry implements MappedPoseInterface, RimoGetListener {
  static final Scalar HALF = DoubleScalar.of(0.5);
  // ---
  final Scalar dt = RimoSocket.getGetPeriod(); // 1/250[s]
  Tensor state;
  /** initial quality value == 0 */
  private Scalar quality = RealScalar.ZERO;

  GokartPoseOdometry(Tensor state) {
    this.state = state.copy();
  }

  @Override // from RimoGetListener
  public final void getEvent(RimoGetEvent rimoGetEvent) {
    step(rimoGetEvent.getAngularRate_Y_pair());
  }

  @Override // from GokartPoseInterface
  public final Tensor getPose() {
    return state.unmodifiable();
  }

  @Override
  public final GokartPoseEvent getPoseEvent() {
    return GokartPoseEvents.getPoseEvent(state, quality);
  }

  @Override
  public final synchronized void setPose(Tensor pose, Scalar quality) {
    // TODO this is not good design: odometry should always be consistent integration of wheels!
    // other entities may track different poses
    state = pose.copy();
    this.quality = quality;
  }

  final synchronized void step(Tensor angularRate_Y_pair) {
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
  abstract Flow singleton(Scalar speedL, Scalar speedR, Scalar yHalfWidth);
}
