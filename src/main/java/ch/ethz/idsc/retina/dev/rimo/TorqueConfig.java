// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PI controller of torque control */
public class TorqueConfig implements Serializable {
  public static final TorqueConfig GLOBAL = AppResources.load(new TorqueConfig());

  private TorqueConfig() {
  }

  // ---
  public Scalar Kp = RealScalar.of(1); // 5
  public Scalar Ki = RealScalar.of(1); // 0.5 hits the saturation limit of 0.5
  public Scalar torqueLimit = RealScalar.of(300);

  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }
}
