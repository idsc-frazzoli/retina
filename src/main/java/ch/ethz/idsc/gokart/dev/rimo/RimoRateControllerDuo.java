// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** the "duo" controller was first used in the pure pursuit module
 * for trajectory following. however, the analysis of the torque
 * showed that the load was not balanced between the motors in a
 * desirable way.
 * 
 * We learned that the torques assigned to both motors add up to
 * an effective torque that determines the longitudinal acceleration
 * <pre>
 * torque_L + torque_R == torque_effective
 * </pre>
 * 
 * Thus, a hidden imbalance can be created by adding an offset to one
 * side, while subtracting that amount from the other engine. We
 * refer to this as the nullspace of the effective torque.
 * <pre>
 * (torque_L + offset) + (torque_R - offset) == torque_effective
 * </pre>
 * 
 * The consequence of that observation is the implementation of
 * {@link RimoRateControllerUno} */
public class RimoRateControllerDuo extends RimoRateControllerWrap {
  private final RimoRateController piL = new SimpleRimoRateController(RimoConfig.GLOBAL);
  private final RimoRateController piR = new SimpleRimoRateController(RimoConfig.GLOBAL);

  @Override // from RimoRateControllerWrap
  protected RimoPutEvent protected_getRimoPutEvent(Scalar rate_target, Scalar angle, RimoGetEvent rimoGetEvent) {
    DifferentialSpeed differentialSpeed = ChassisGeometry.GLOBAL.getDifferentialSpeed();
    Tensor pair = differentialSpeed.pair(rate_target, angle);
    short armsL_raw = 0;
    short armsR_raw = 0;
    {
      Scalar vel_targetL = pair.Get(0);
      Scalar vel_error = vel_targetL.subtract(rimoGetEvent.getTireL.getAngularRate_Y());
      Scalar torque = piL.iterate(vel_error);
      short valueL_Yaxis = Magnitude.ARMS.toShort(torque);
      armsL_raw = (short) -valueL_Yaxis; // negative sign LEFT
    }
    {
      Scalar vel_targetR = pair.Get(1);
      Scalar vel_error = vel_targetR.subtract(rimoGetEvent.getTireR.getAngularRate_Y());
      Scalar torque = piR.iterate(vel_error);
      short valueR_Yaxis = Magnitude.ARMS.toShort(torque);
      armsR_raw = (short) +valueR_Yaxis; // positive sign RIGHT
    }
    return RimoPutHelper.operationTorque(armsL_raw, armsR_raw);
  }
}
