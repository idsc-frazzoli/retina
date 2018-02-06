// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class RimoRateControllerDuo extends RimoRateControllerWrap {
  private final RimoRateController piL = new RimoRateController();
  private final RimoRateController piR = new RimoRateController();

  @Override
  protected RimoPutEvent protected_getRimoPutEvent(Scalar vel_target, Scalar theta, RimoGetEvent rimoGetEvent) {
    DifferentialSpeed differentialSpeed = ChassisGeometry.GLOBAL.getDifferentialSpeed();
    Tensor pair = differentialSpeed.pair(vel_target, theta);
    short armsL_raw = 0;
    short armsR_raw = 0;
    {
      Scalar vel_targetL = pair.Get(0);
      Scalar vel_error = vel_targetL.subtract(rimoGetEvent.getTireL.getAngularRate_Y());
      Scalar torque = piL.iterate(vel_error);
      short valueL_Yaxis = RimoPutTire.MAGNITUDE_ARMS.apply(torque).number().shortValue();
      armsL_raw = (short) -valueL_Yaxis; // negative sign LEFT
    }
    {
      Scalar vel_targetR = pair.Get(1);
      Scalar vel_error = vel_targetR.subtract(rimoGetEvent.getTireR.getAngularRate_Y());
      Scalar torque = piR.iterate(vel_error);
      short valueR_Yaxis = RimoPutTire.MAGNITUDE_ARMS.apply(torque).number().shortValue();
      armsR_raw = (short) +valueR_Yaxis; // positive sign RIGHT
    }
    return new RimoPutEvent( //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
    );
  }
}
