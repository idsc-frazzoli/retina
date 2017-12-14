// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
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

  private LinmotConfig() {
  }

  /***************************************************/
  public Scalar windingTempCold = Quantity.of(5, LinmotGetEvent.CELSIUS);
  public Scalar windingTempGlow = Quantity.of(85, LinmotGetEvent.CELSIUS);
  public Scalar windingTempFire = Quantity.of(110, LinmotGetEvent.CELSIUS);

  /***************************************************/
  public Clip temperatureOperationClip() {
    return Clip.function(windingTempCold, windingTempGlow);
  }

  public Clip temperatureHardwareClip() {
    return Clip.function(windingTempCold, windingTempFire);
  }

  public boolean isTemperatureOperationSafe(LinmotGetEvent linmotGetEvent) {
    return temperatureOperationClip().isInside(linmotGetEvent.getWindingTemperatureMax());
  }

  public boolean isTemperatureHardwareSafe(LinmotGetEvent linmotGetEvent) {
    return temperatureHardwareClip().isInside(linmotGetEvent.getWindingTemperatureMax());
  }
}
