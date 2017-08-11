// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.DavisImuEventListener;
import ch.ethz.idsc.retina.davis.DavisImuFrameListener;

public class DavisImuProvider implements DavisImuEventListener {
  private static float accelSensitivityScaleFactorGPerLsb = 1f / 8192;
  private static float gyroSensitivityScaleFactorDegPerSecPerLsb = 1f / 65.5f;
  private static float temperatureScaleFactorDegCPerLsb = 1f / 340;
  private static float temperatureOffsetDegC = 35;
  // ---
  private final float[] values = new float[7];
  private final List<DavisImuFrameListener> listeners = new LinkedList<>();
  // public void addListener(ColumnTimedImageListener timedImageListener) {
  // timedImageListeners.add(timedImageListener);
  // }

  @Override
  public void imu(DavisImuEvent imuDavisEvent) {
    int ordinal = imuDavisEvent.index;
    switch (ordinal) {
    case 0:
    case 1:
    case 2: {
      values[ordinal] = imuDavisEvent.value * accelSensitivityScaleFactorGPerLsb;
      break;
    }
    case 3: {
      values[ordinal] = imuDavisEvent.value * temperatureScaleFactorDegCPerLsb + temperatureOffsetDegC;
      break;
    }
    case 4:
    case 5:
    case 6: {
      values[ordinal] = imuDavisEvent.value * gyroSensitivityScaleFactorDegPerSecPerLsb;
      break;
    }
    default:
      break;
    }
    if (imuDavisEvent.index == 6) {
      // SIGNS ARE INTENTED
      DavisImuFrame imuRecord = new DavisImuFrame(-values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
      listeners.forEach(listener -> listener.imuRecord(imuRecord));
      // imuRecord.print();
    }
  }
}
