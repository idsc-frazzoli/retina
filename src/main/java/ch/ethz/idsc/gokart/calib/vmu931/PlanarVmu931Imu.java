// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** .
 * ante 20190408: the vmu931 was mounted on the gokart with xyz aligned with the gokart coordinate system
 * post 20190408: the vmu931 is mounted rotated around U axis with 180[deg] */
public interface PlanarVmu931Imu {
  /** @param vmu931ImuFrame
   * @return vector of length 2 of acceleration in gokart coordinates */
  Tensor vmu931AccXY(Vmu931ImuFrame vmu931ImuFrame);

  Tensor vmu931AccXY(Tensor accXY);

  /** @param vmu931ImuFrame
   * @return rotational rate around gokart Z axis quantity with unit [s^-1] */
  Scalar vmu931GyroZ(Vmu931ImuFrame vmu931ImuFrame);

  Scalar vmu931GyroZ(Scalar gyroZ);
}
