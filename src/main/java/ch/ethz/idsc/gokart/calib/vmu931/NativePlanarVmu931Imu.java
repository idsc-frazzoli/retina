// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** ante 20190208 */
/* package */ enum NativePlanarVmu931Imu implements PlanarVmu931Imu {
  INSTANCE;
  // ---
  @Override // from PlanarVmu931Imu
  public Tensor accXY(Vmu931ImuFrame vmu931ImuFrame) {
    return accXY(vmu931ImuFrame.accXY());
  }

  @Override // from PlanarVmu931Imu
  public Tensor accXY(Tensor accXY) {
    return accXY.copy();
  }

  @Override // from PlanarVmu931Imu
  public Tensor acceleration(Vmu931ImuFrame vmu931ImuFrame) {
    return vmu931ImuFrame.acceleration();
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
    return vmu931ImuFrame.gyroscope();
  }
}
