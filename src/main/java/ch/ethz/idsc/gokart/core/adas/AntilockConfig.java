// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.io.Serializable;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** Reference:
 * "Advanced Driver Assistance Systems on a Go-Kart" by A. Mosberger */
public class AntilockConfig implements Serializable {
  public static final AntilockConfig GLOBAL = AppResources.load(new AntilockConfig());
  /***************************************************/
  /** Values for AntilockBrakeModule */
  /** access value via {@link #criticalAngle()} */
  public Scalar minSlip = Quantity.of(0.7, SI.PER_SECOND);
  public Scalar maxSlip = Quantity.of(1.4, SI.PER_SECOND);
  /** minSlip and maxSlip depending on the current velocity */
  public Scalar minSlipTheory = RealScalar.of(0.15);
  public Scalar maxSlipTheory = RealScalar.of(0.25);
  public Scalar fullBraking = RealScalar.of(0.9);
  public Scalar incrBraking = RealScalar.of(0.005);
  public Scalar criticalAngle = Quantity.of(12, NonSI.DEGREE_ANGLE);
  /** set velocity for a full stop with or without anti-lock braking */
  @FieldSubdivide(start = "5.75[m*s^-1]", end = "8.5[m*s^-1]", intervals = 11)
  public Scalar setVel = Quantity.of(6.5, SI.VELOCITY);

  /***************************************************/
  // functions for anti-lock brake
  public Scalar criticalAngle() {
    return UnitSystem.SI().apply(criticalAngle);
  }

  public Clip slipClip() {
    return Clips.interval(minSlip, maxSlip);
  }
}
