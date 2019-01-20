// code by jph
package ch.ethz.idsc.gokart.core;

import ch.ethz.idsc.gokart.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.data.DataEvent;

public abstract class AutoboxCalibrationProvider<PE extends DataEvent> extends AutoboxScheduledProvider<PE> {
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  /** the application layer is discouraged to {@link #schedule()}
   * the calibration procedure unless function returns true
   * 
   * Exception: {@link SteerCalibrationProvider}
   * 
   * @return true if calibration is known to be required */
  public final boolean isScheduleSuggested() {
    return isIdle() //
        && hintScheduleRequired();
  }

  /** function should return false if no information is present
   * to determine state of actuator
   * 
   * @return true if calibration is known to be required */
  protected abstract boolean hintScheduleRequired();
}
