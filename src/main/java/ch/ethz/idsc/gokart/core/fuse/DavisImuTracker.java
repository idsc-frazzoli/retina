// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

public enum DavisImuTracker implements DavisImuFrameListener {
  INSTANCE;
  // ---
  /** the number of 20 means that the estimate is computed
   * as the mean over the last 20 gyro measurements */
  private final Tensor gyroZ = Array.of(l -> Quantity.of(0.0, SI.PER_SECOND), 20);
  private int index = 0;
  private int framecount = 0;

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    gyroZ.set(SensorsConfig.GLOBAL.getGyroZ(davisImuFrame), index);
    ++index;
    index %= gyroZ.length();
    ++framecount;
  }

  /** @return fairly accurate estimate of gyro rate of gokart around z-axis in unit "s^-1" */
  public Scalar getGyroZ() {
    return Mean.of(gyroZ).Get();
  }

  /** @return */
  public int getFramecount() {
    return framecount;
  }

  /** Important: function exists only for testing!
   * 
   * DO NOT CALL FUNCTION DURING OPERATION
   * 
   * @param value with unit s^-1 */
  public void setGyroZ(Scalar value) {
    Magnitude.PER_SECOND.apply(value); // consistency check
    gyroZ.set(l -> value, Tensor.ALL);
  }
}
