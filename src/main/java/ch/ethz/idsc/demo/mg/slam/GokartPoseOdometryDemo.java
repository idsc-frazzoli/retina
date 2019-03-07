// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/** odometry is the integration of the wheels speeds to obtain the pose of the go kart
 * due to the high quality of the wheel/motor encoders the odometry turns out to be
 * quite smooth and stable.
 * 
 * <p>Naturally, any tire slip results in a loss of tracking accuracy. */
// DEMO class which provides velocity such that it can be integrated into the SLAM algorithm
// rad 0.14, ytir = 0.65 very good rotation tracking! but speed not accurate
// rad 0.12, ytir = 0.54 good speed tracking, rotation ok
// TODO MG lots of commonality with GokartPoseOdometry -> unify
public class GokartPoseOdometryDemo implements GokartPoseInterface, RimoGetListener {
  public static GokartPoseOdometryDemo create(Tensor state) {
    return new GokartPoseOdometryDemo(state);
  }

  /** @return with initial pose {0[m], 0[m], 0} */
  public static GokartPoseOdometryDemo create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }

  // ---
  private static final Tensor VELOCITY_INIT = Tensors.fromString("{0[m*s^-1],0[m*s^-1],0[s^-1]}").unmodifiable();
  // ---
  private final Scalar dt = RimoSocket.getGetPeriod(); // 1/250[s] update period
  // ---
  private Tensor state;
  /** velocity is the tangent of the state {vx[m*s^-1], 0[m*s^-1], angular_rate[s^-1]} */
  private Tensor velocity = VELOCITY_INIT;

  private GokartPoseOdometryDemo(Tensor state) {
    this.state = state.copy();
  }

  /** @param pose of the form {x[m], y[m], heading} */
  public void setPose(Tensor pose) {
    GokartPoseHelper.toUnitless(pose); // checks units
    this.state = pose.copy();
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    step(rimoGetEvent.getAngularRate_Y_pair());
  }

  /** @param angularRate_Y_pair */
  /* package */ synchronized void step(Tensor angularRate_Y_pair) {
    velocity = ChassisGeometry.GLOBAL.odometryVelocity(angularRate_Y_pair);
    Flow flow = singleton(velocity);
    state = Se2CarIntegrator.INSTANCE.step(flow, state, dt);
  }

  /** @return {vx[m*s^-1], 0[m*s^-1], omega[s^-1]} */
  /* package */ Tensor getVelocity() {
    return velocity;
  }

  /** @return velocity unitless representation */
  public Tensor getVelocityUnitless() {
    return Tensors.of( //
        Magnitude.VELOCITY.apply(velocity.Get(0)), //
        Magnitude.VELOCITY.apply(velocity.Get(1)), //
        Magnitude.PER_SECOND.apply(velocity.Get(2)));
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return state.unmodifiable();
  }

  /** @param velocity vector of the form {vx[m*s^-1], 0[m*s^-1], omega[s^-1]}
   * @return */
  private static Flow singleton(Tensor velocity) {
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, N.DOUBLE.of(velocity));
  }
}
