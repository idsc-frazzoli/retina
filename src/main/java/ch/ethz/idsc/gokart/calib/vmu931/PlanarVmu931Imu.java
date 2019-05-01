// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** .
 * ante 20190208: the vmu931 was mounted on the gokart with xyz aligned with the gokart coordinate system
 * post 20190208: the vmu931 is mounted rotated around U axis with 180[deg] */
public interface PlanarVmu931Imu {
  /** @param vmu931ImuFrame
   * @return vector of length 2 of acceleration in gokart coordinates */
  Tensor accXY(Vmu931ImuFrame vmu931ImuFrame);

  Tensor accXY(Tensor accXY);

  /** @param vmu931ImuFrame
   * @return {accX[m*s^-2], accY[m*s^-2], accZ[m*s^-2]} */
  Tensor acceleration(Vmu931ImuFrame vmu931ImuFrame);

  /** @param vmu931ImuFrame
   * @return rotational rate around gokart Z axis quantity with unit [s^-1] */
  Scalar gyroZ(Vmu931ImuFrame vmu931ImuFrame);

  Scalar gyroZ(Scalar gyroZ);

  /** @param vmu931ImuFrame
   * @return {gyroX[s^-1], gyroY[s^-1], gyroZ[s^-1]} */
  Tensor gyroscope(Vmu931ImuFrame vmu931ImuFrame);
}
