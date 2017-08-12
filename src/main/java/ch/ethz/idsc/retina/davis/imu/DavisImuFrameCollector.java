// code by jph
package ch.ethz.idsc.retina.davis.imu;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.DavisImuEventListener;
import ch.ethz.idsc.retina.davis._240c.DavisImuEvent;

/** the conversion formulas are trimmed to match the values in the jAER demo
 * therefore the absolute values should be correct
 * the ordering */
public class DavisImuFrameCollector implements DavisImuEventListener {
  private static float accelSensitivityScaleFactorGPerLsb = 2f / 8192;
  private static float temperatureScaleFactorDegCPerLsb = 1f / 340;
  private static float gyroSensitivityScaleFactorDegPerSecPerLsb = 2f / 65.5f;
  private static float temperatureOffsetDegC = 35;
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
    case 2: {
      value[ordinal] = davisImuEvent.value * accelSensitivityScaleFactorGPerLsb;
      break;
    }
    case 3: {
      value[ordinal] = davisImuEvent.value * temperatureScaleFactorDegCPerLsb + temperatureOffsetDegC;
      break;
    }
    case 4:
    case 5:
    case 6: {
      value[ordinal] = davisImuEvent.value * gyroSensitivityScaleFactorDegPerSecPerLsb;
      break;
    }
    default:
      break;
    }
    if (davisImuEvent.index == 6) {
      // the sign flips are selected to match the numbers in the jAER demo
      value[0] = -value[0];
      value[4] = -value[4];
      value[6] = -value[6];
      DavisImuFrame davisImuFrame = new DavisImuFrame(davisImuEvent.time, value);
      listeners.forEach(listener -> listener.imuFrame(davisImuFrame));
    }
  }
}
