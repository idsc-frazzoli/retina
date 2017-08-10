// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.ImuDavisEventListener;
import ch.ethz.idsc.retina.davis.ImuRecordListener;

public class DavisImuProvider implements ImuDavisEventListener {
  private static float accelSensitivityScaleFactorGPerLsb = 1f / 8192;
  private static float gyroSensitivityScaleFactorDegPerSecPerLsb = 1f / 65.5f;
  private static float temperatureScaleFactorDegCPerLsb = 1f / 340;
  private static float temperatureOffsetDegC = 35;
  // ---
  private final float[] values = new float[7];
  private final List<ImuRecordListener> list = new LinkedList<>();

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
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
      ImuRecord imuRecord = new ImuRecord(-values[0], values[1], values[2], values[3], values[4], values[5], values[6]);
      list.forEach(listener -> listener.imuRecord(imuRecord));
      // imuRecord.print();
    }
  }
}
