// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PD controller of steering */
public class SteerConfig implements Serializable {
  public static final SteerConfig GLOBAL = AppResources.load(new SteerConfig());

  private SteerConfig() {
  }

  /***************************************************/
  public Scalar calibration = Quantity.of(1.0, "N*m");
  public Scalar Ki = Quantity.of(5.7, "SCE^-1*N*m*s^-1");
  public Scalar Kp = Quantity.of(7.2, "SCE^-1*N*m");
  public Scalar Kd = Quantity.of(0.82, "SCE^-1*N*m*s");
  public Scalar torqueLimit = Quantity.of(1.5, "N*m");
  // ---
  /** conversion factor from measured steer column angle to front wheel angle */
  public Scalar column2steer = Quantity.of(0.6, "rad*SCE^-1");
  public Scalar stepPercent = RealScalar.of(0.5);

  /***************************************************/
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }

  public static Scalar getAngleFromSCE(Scalar steerColumnAngle) {
    return UnitSystem.SI().apply( //
        Quantity.of(steerColumnAngle, SteerPutEvent.UNIT_ENCODER) //
            .multiply(SteerConfig.GLOBAL.column2steer));
  }
}
