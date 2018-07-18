// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;

/** odometry is the integration of the wheels speeds to obtain the pose of the go kart
 * due to the high quality of the wheel/motor encoders the odometry turns out to be
 * quite smooth and stable.
 * 
 * <p>Naturally, any tire slip results in a loss of tracking accuracy. */
// DEMO class which provides velocity such that it can be integrated into the SLAM algorithm
// rad 0.14, ytir = 0.65 very good rotation tracking! but speed not accurate
// rad 0.12, ytir = 0.54 good speed tracking, rotation ok
class GokartPoseOdometryDemo implements GokartPoseInterface, RimoGetListener {
  private static final Scalar HALF = DoubleScalar.of(0.5);

  public static GokartPoseOdometryDemo create(Tensor state) {
    return new GokartPoseOdometryDemo(state);
  }

  public static GokartPoseOdometryDemo create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }

  private final Scalar dt = RimoSocket.INSTANCE.getGetPeriod(); // 1/250[s] update period
  private final Scalar radius = ChassisGeometry.GLOBAL.tireRadiusRear;
  private final Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRear;
  private Tensor state;
  private Tensor velocity;

  private GokartPoseOdometryDemo(Tensor state) {
    this.state = state.copy();
  }

  public void initializePose(Tensor pose) {
    this.state = pose.copy();
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    step(rimoGetEvent.getAngularRate_Y_pair());
  }

  /* package */ synchronized void step(Tensor angularRate_Y_pair) {
    Tensor speed_pair = angularRate_Y_pair.multiply(radius);
    velocity = GokartPoseOdometryDemo.computeVelocity(speed_pair.Get(0), speed_pair.Get(1), yTireRear);
    Flow flow = GokartPoseOdometryDemo.singleton(velocity);
    state = Se2CarIntegrator.INSTANCE.step(flow, state, dt);
  }

  public static Tensor computeVelocity(Scalar speedL, Scalar speedR, Scalar yHalfWidth) {
    Scalar speed = speedL.add(speedR).multiply(HALF);
    Scalar rate = speedR.subtract(speedL).multiply(HALF).divide(yHalfWidth);
    return Tensors.of(speed, Quantity.of(0, SI.VELOCITY), rate);
  }

  public static Flow singleton(Tensor velocity) {
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(velocity));
  }

  public Tensor getVelocity() {
    return velocity;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return state.unmodifiable();
  }
}
