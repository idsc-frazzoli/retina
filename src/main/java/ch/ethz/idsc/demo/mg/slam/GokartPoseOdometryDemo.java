// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** odometry is the integration of the wheels speeds to obtain the pose of the go kart
 * due to the high quality of the wheel/motor encoders the odometry turns out to be
 * quite smooth and stable.
 * 
 * <p>Naturally, any tire slip results in a loss of tracking accuracy. */
// this is a demo class for wheel odometry. It implements GokartPoseInterface instead of MappedPoseInterface.
// TODO add method that provides "delta_pose" for a variable dt
// TODO long term: similarity with GokartPoseOdometry
class GokartPoseOdometryDemo implements GokartPoseInterface, RimoGetListener {
  // private static final Scalar HALF = DoubleScalar.of(0.5);
  public static GokartPoseOdometryDemo create(Tensor state) {
    return new GokartPoseOdometryDemo(state);
  }

  public static GokartPoseOdometryDemo create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }
  // ---

  private final Scalar dt = RimoSocket.INSTANCE.getGetPeriod(); // 1/250[s] update period
  private Tensor currentState; // forward integrated since beginning until current event
  private Tensor lastState; // forward integrated state until last event
  private Tensor deltaState; // delta state between last two RimoGetEvents

  private GokartPoseOdometryDemo(Tensor state) {
    this.currentState = state.copy();
    this.lastState = state.copy();
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
    Flow flow = GokartPoseOdometry.singleton(speed_pair.Get(0), speed_pair.Get(1), yTireRear);
    currentState = Se2CarIntegrator.INSTANCE.step(flow, currentState, dt);
    deltaState = currentState.subtract(lastState);
    lastState = currentState;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return currentState.unmodifiable();
  }

  public Tensor getDeltaPose() {
    return deltaState;
  }
}
