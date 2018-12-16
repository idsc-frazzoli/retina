// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

/** parameters for PI controller of torque control */
public class RimoConfig {
  public static final RimoConfig GLOBAL = AppResources.load(new RimoConfig());
  /***************************************************/
  /** parameters for {@link SimpleRimoRateController}
   * rateLimit, Kp, Ki */
  public Scalar rateLimit = Quantity.of(20, "rad*s^-1"); // <- DEPRECATED
  public Scalar Kp = Quantity.of(35, "ARMS*rad^-1*s"); // 40
  public Scalar Ki = Quantity.of(0, "ARMS*rad^-1"); // 15
  /** used for lookup table */
  public Scalar lKp = Quantity.of(0, SI.ACCELERATION.add(SI.VELOCITY.negate()));
  public Scalar lKi = Quantity.of(1, SI.ACCELERATION.add(SI.METER.negate()));
  public Scalar lAntiWindupPadding = Quantity.of(0.1, SI.ACCELERATION);
  /** constant for anti wind-up used by revised rimo rate controller */
  public Scalar Kawu = RealScalar.of(0);
  /** the physical maximum torque limit is 2316[ARMS]
   * the torque limit is used in RimoTorqueJoystickModule */
  public Scalar torqueLimit = Quantity.of(1500, NonSI.ARMS);
  /** corresponds to tangent speed of 5[cm*s^-1] */
  public Scalar speedChop = Quantity.of(0.05, SI.VELOCITY);

  /***************************************************/
  /** @return clip interval for permitted torque */
  public Clip torqueLimitClip() {
    return Clip.function(torqueLimit.negate(), torqueLimit);
  }

  /** @return chop for tangent speed values */
  public Chop speedChop() {
    return Chop.below(Magnitude.VELOCITY.toDouble(speedChop));
  }
}
