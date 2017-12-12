// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public class GokartOdometry implements RimoGetListener {
  private static final Scalar HALF = DoubleScalar.of(0.5);

  public static GokartOdometry create(Tensor state) {
    return new GokartOdometry(state);
  }

  public static GokartOdometry create() {
    return create(Tensors.fromString("{0[m], 0[m], 0}"));
  }

  // ---
  private final Scalar dt = RimoSocket.INSTANCE.getGetPeriod(); // 1/250[s]
  private Tensor state;

  private GokartOdometry(Tensor state) {
    this.state = state.copy();
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    step(rimoGetEvent.getAngularRate_Y_pair());
  }

  /* package */ void step(Tensor angularRate_Y_pair) {
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
   * @param yHalfWidth "m*rad^-1"
   * @return */
  /* package */ Flow singleton(Scalar speedL, Scalar speedR, Scalar yHalfWidth) {
    Scalar speed = speedL.add(speedR).multiply(HALF);
    Scalar rate = speedR.subtract(speedL).multiply(HALF).divide(yHalfWidth);
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate)));
  }

  public Tensor getState() {
    return state.unmodifiable();
  }
}
