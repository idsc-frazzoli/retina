// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;

public enum GokartActuatorCalibration {
  ;
  public static void all() {
    if (MiscIgnitionProvider.INSTANCE.isScheduleSuggested())
      MiscIgnitionProvider.INSTANCE.schedule(); // reset misc comm
    // ---
    if (LinmotCalibrationProvider.INSTANCE.isScheduleSuggested())
      LinmotCalibrationProvider.INSTANCE.schedule(); // calibrate linmot
    // ---
    if (SteerCalibrationProvider.INSTANCE.isScheduleSuggested())
      SteerCalibrationProvider.INSTANCE.schedule(); // calibrate steering
  }
}
