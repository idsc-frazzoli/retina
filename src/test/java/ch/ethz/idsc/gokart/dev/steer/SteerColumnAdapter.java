// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.qty.QuantityUnit;

public class SteerColumnAdapter implements SteerColumnInterface {
  private final boolean isCalibrated;
  private final Scalar centered;

  /** @param isCalibrated
   * @param centered Quantity with unit "SCE" */
  public SteerColumnAdapter(boolean isCalibrated, Scalar centered) {
    if (!QuantityUnit.of(centered).equals(SteerPutEvent.UNIT_ENCODER))
      throw TensorRuntimeException.of(centered);
    this.isCalibrated = isCalibrated;
    this.centered = centered;
  }

  @Override // from SteerColumnInterface
  public boolean isSteerColumnCalibrated() {
    return isCalibrated;
  }

  @Override // from SteerColumnInterface
  public Scalar getSteerColumnEncoderCentered() {
    return centered;
  }
}
