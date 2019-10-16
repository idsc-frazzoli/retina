// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;

/** post 20190521 since GokartLogFile _20190521T150634_d2699045 */
/* package */ enum Rot90PlanarVmu931Imu implements PlanarVmu931Imu {
  INSTANCE;
  // ---
  @Override // from PlanarVmu931Imu
  public Tensor accXY(Vmu931ImuFrame vmu931ImuFrame) {
    return accXY(vmu931ImuFrame.accXY());
  }

  @Override // from PlanarVmu931Imu
  public Tensor accXY(Tensor accXY) {
    return Cross.of(accXY);
  }

  @Override // from PlanarVmu931Imu
  public Tensor acceleration(Vmu931ImuFrame vmu931ImuFrame) {
    Tensor accXY = vmu931ImuFrame.acceleration();
    return Tensors.of(accXY.Get(1).negate(), accXY.Get(0), accXY.Get(2));
  }

  @Override // from PlanarVmu931Imu
  public Scalar gyroZ(Vmu931ImuFrame vmu931ImuFrame) {
    return gyroZ(vmu931ImuFrame.gyroZ());
  }

  @Override // from PlanarVmu931Imu
  public Scalar gyroZ(Scalar gyroZ) {
    return gyroZ;
  }

  @Override // from PlanarVmu931Imu
  public Tensor gyroscope(Vmu931ImuFrame vmu931ImuFrame) {
    Tensor gyro = vmu931ImuFrame.gyroscope();
    return Tensors.of(gyro.Get(1).negate(), gyro.Get(0), gyro.Get(2));
  }
}
