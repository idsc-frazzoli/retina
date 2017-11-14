// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PD controller of steering */
public class SteerConfig implements Serializable {
  public static final SteerConfig GLOBAL = AppResources.load(new SteerConfig());

  private SteerConfig() {
  }

  /***************************************************/
  public Scalar calibration = RealScalar.of(0.2);
  public Scalar Kp = RealScalar.of(2.5); // 5
  public Scalar Kd = RealScalar.of(0.2); // 0.5 hits the saturation limit of 0.5
  public Scalar torqueLimit = RealScalar.of(0.5);
  // ---
  /** conversion factor from measured steer column angle to front wheel angle */
  public Scalar column2steer = RealScalar.of(1.0);

  /***************************************************/
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }
}
