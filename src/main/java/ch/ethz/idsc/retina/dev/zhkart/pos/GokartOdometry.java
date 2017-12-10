// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.N;

public class GokartOdometry implements RimoGetListener {
  private static final Scalar HALF = DoubleScalar.of(0.5);
  // ---
  private final Scalar dt = RimoSocket.INSTANCE.getPeriod(); // TODO assumption
  private Tensor state = Array.zeros(3);

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    step(rimoGetEvent.getAngularRate_Y_pair());
  }

  void step(Tensor angularRate_Y_pair) {
    Scalar radius = ChassisGeometry.GLOBAL.tireRadiusRear;
    Tensor speed_pair = angularRate_Y_pair.multiply(radius); // [rad*s^-1] * [m*rad^-1] == [m*s^-1]
    Integrator integrator = Se2CarIntegrator.INSTANCE;
    Flow flow = singleton(speed_pair.Get(0), speed_pair.Get(1), ChassisGeometry.GLOBAL.yTireRear);
    state = integrator.step(flow, state, dt);
  }

  /** .
   * @param speedL with unit "m*s^-1"
   * @param speedR with unit "m*s^-1"
   * @param halfWidth "m*rad^-1"
   * @return */
  Flow singleton(Scalar speedL, Scalar speedR, Scalar halfWidth) {
    Scalar speed = speedL.add(speedR);
    Scalar rate = speedR.subtract(speedL).multiply(HALF).divide(halfWidth);
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate)));
  }

  public Tensor getState() {
    return state.unmodifiable();
  }
}
