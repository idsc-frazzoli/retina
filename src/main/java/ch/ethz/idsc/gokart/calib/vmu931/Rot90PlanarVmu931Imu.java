// code by jph
package ch.ethz.idsc.gokart.calib.vmu931;

import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** post 20190521 */
public enum Rot90PlanarVmu931Imu implements PlanarVmu931Imu {
  INSTANCE;
  // ---
  @Override // from PlanarVmu931Imu
  public Tensor accXY(Vmu931ImuFrame vmu931ImuFrame) {
    return accXY(vmu931ImuFrame.accXY());
  }

  @Override // from PlanarVmu931Imu
  public Tensor accXY(Tensor accXY) {
    // TODO replace with Cross
    return Tensors.of(accXY.Get(1).negate(), accXY.Get(0));
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
    // FIXME this should be rotated
    return vmu931ImuFrame.gyroscope();
  }
}
