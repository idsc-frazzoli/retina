// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** post [20190208 */
/* package */ enum FlippedPlanarVmu931Imu implements PlanarVmu931Imu {
  INSTANCE;
  // ---
  @Override // from PlanarVmu931Imu
  public Tensor accXY(Vmu931ImuFrame vmu931ImuFrame) {
    return accXY(vmu931ImuFrame.accXY());
  }

  @Override // from PlanarVmu931Imu
  public Tensor accXY(Tensor accXY) {
    return Tensors.of(accXY.Get(1).negate(), accXY.Get(0).negate());
  }

  @Override // from PlanarVmu931Imu
  public Tensor acceleration(Vmu931ImuFrame vmu931ImuFrame) {
    return negate_yxz(vmu931ImuFrame.acceleration());
  }

  @Override // from PlanarVmu931Imu
  public Scalar gyroZ(Vmu931ImuFrame vmu931ImuFrame) {
    return gyroZ(vmu931ImuFrame.gyroZ());
  }

  @Override // from PlanarVmu931Imu
  public Scalar gyroZ(Scalar gyroZ) {
    return gyroZ.negate();
  }

  @Override // from PlanarVmu931Imu
  public Tensor gyroscope(Vmu931ImuFrame vmu931ImuFrame) {
    return negate_yxz(vmu931ImuFrame.gyroscope());
  }

  private static Tensor negate_yxz(Tensor xyz) {
    return Tensors.of( //
        xyz.Get(1).negate(), //
        xyz.Get(0).negate(), //
        xyz.Get(2).negate());
  }
}
