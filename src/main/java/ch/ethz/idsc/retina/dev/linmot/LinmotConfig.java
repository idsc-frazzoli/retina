// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class LinmotConfig implements Serializable {
  public static final LinmotConfig GLOBAL = AppResources.load(new LinmotConfig());
  /***************************************************/
  public Scalar windingTempCold = Quantity.of(5, SI.DEGREE_CELSIUS);
  public Scalar windingTempGlow = Quantity.of(85, SI.DEGREE_CELSIUS);
  public Scalar windingTempFire = Quantity.of(110, SI.DEGREE_CELSIUS);

  /***************************************************/
  public Clip temperatureOperationClip() {
    return Clip.function(windingTempCold, windingTempGlow);
  }

  public Clip temperatureHardwareClip() {
    return Clip.function(windingTempCold, windingTempFire);
  }

  public boolean isTemperatureOperationSafe(Scalar temperature) {
    return temperatureOperationClip().isInside(temperature);
  }

  public boolean isTemperatureHardwareSafe(Scalar temperature) {
    return temperatureHardwareClip().isInside(temperature);
  }

  /** bounds established using experimentation */
  // TODO make 20000 configurable
  public static final Clip NOMINAL_POSITION_DELTA = Clip.function(-20000, 20000);
}
