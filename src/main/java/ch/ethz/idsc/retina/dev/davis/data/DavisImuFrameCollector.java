// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.DavisImuEventListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisImuEvent;

/** the conversion formulas are trimmed to match the values in the jAER demo
 * therefore the absolute values should be correct
 * the ordering */
public class DavisImuFrameCollector implements DavisImuEventListener {
  private static final double GM_S2 = 9.81;
  private static final float accelSensitivityScaleFactorGPerLsb = (float) (GM_S2 * 2.0 / 8192);
  private static final float temperatureScaleFactorDegCPerLsb = 1f / 340;
  private static final float gyroSensitivityScaleFactorDegPerSecPerLsb = 2f / 65.5f;
  private static final float temperatureOffsetDegC = 35;
  // ---
  private final float[] value = new float[7];
  private final List<DavisImuFrameListener> listeners = new LinkedList<>();

  public void addListener(DavisImuFrameListener davisImuFrameListener) {
    listeners.add(davisImuFrameListener);
  }

  @Override
  public void imu(DavisImuEvent davisImuEvent) {
    int ordinal = davisImuEvent.index;
    switch (ordinal) {
    case 0:
    case 1:
    case 2:
      value[ordinal] = davisImuEvent.value * accelSensitivityScaleFactorGPerLsb;
      break;
    case 3:
      value[ordinal] = davisImuEvent.value * temperatureScaleFactorDegCPerLsb + temperatureOffsetDegC;
      break;
    case 4:
    case 5:
    case 6:
      value[ordinal] = davisImuEvent.value * gyroSensitivityScaleFactorDegPerSecPerLsb;
      break;
    default:
      System.err.println("unknown imu index " + ordinal);
    }
    if (davisImuEvent.index == 6) {
      value[1] = -value[1]; // accel y-axis flip
      value[4] = -value[4]; // gyro x-axis flip
      value[6] = -value[6]; // gyro z-axis flip
      DavisImuFrame davisImuFrame = new DavisImuFrame(davisImuEvent.time, value);
      listeners.forEach(listener -> listener.imuFrame(davisImuFrame));
    }
  }
}
