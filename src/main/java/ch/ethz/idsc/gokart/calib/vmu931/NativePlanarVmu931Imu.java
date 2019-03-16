// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** ante 20190408 */
public enum NativePlanarVmu931Imu implements PlanarVmu931Imu {
  INSTANCE;
  // ---
  @Override
  public Tensor vmu931AccXY(Vmu931ImuFrame vmu931ImuFrame) {
    return vmu931AccXY(vmu931ImuFrame.accXY());
  }

  @Override
  public Tensor vmu931AccXY(Tensor accRawXY) {
    return accRawXY.copy();
  }

  @Override
  public Scalar vmu931GyroZ(Vmu931ImuFrame vmu931ImuFrame) {
    return vmu931GyroZ(vmu931ImuFrame.gyroZ());
  }

  @Override
  public Scalar vmu931GyroZ(Scalar gyroZ) {
    return gyroZ;
  }
}
