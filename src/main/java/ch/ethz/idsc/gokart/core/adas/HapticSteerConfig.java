// code by am
package ch.ethz.idsc.gokart.core.adas;

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

public class HapticSteerConfig {
  public static final HapticSteerConfig GLOBAL = AppResources.load(new HapticSteerConfig());
  /***************************************************/
  /** Value to amplify the Input in the PowerSteeringModule */
  public Scalar staticCompensation = Quantity.of(0.6, "SCT*SCE^-1");
  public Scalar dynamicCompensation = Quantity.of(90, "SCT");
  public Scalar dynamicCompensationBoundary = Quantity.of(0.5, "SCT");
  public Scalar latForceCompensation = Quantity.of(0.3, "SCT*s*m^-1");
  public Scalar latForceCompensationBoundary = Quantity.of(2, "SCT");
  public Scalar velocityFilter = RealScalar.of(0.2);
  /** Constant Torque for Experiment */
  @FieldSubdivide(start = "-7/10[SCT]", end = "7/10[SCT]", intervals = 14)
  public Scalar constantTorque = Quantity.of(0, "SCT");
  /** Values for Vibration Mode */
  public Scalar vibrationAmplitude = RealScalar.of(0.4);
  public Scalar vibrationFrequency = RealScalar.of(12);
  public Scalar criticalSlip = RealScalar.of(0.2);
  /** Values for AntilockBrakeModule */
  /** access value via {@link #criticalAngle()} */
  public Scalar minSlip = RealScalar.of(0.1);
  public Scalar maxSlip = RealScalar.of(0.25);
  public Scalar fullBraking = RealScalar.of(0.85);
  public Scalar criticalAngleDeg = Quantity.of(12, NonSI.DEGREE_ANGLE);
  public Scalar absFrequency = RealScalar.of(5);
  public Scalar absAmplitude = RealScalar.of(0.2);
  public double absDuration = 1;

  public Scalar criticalAngle() {
    SI.ANGULAR_ACCELERATION.add(SI.VELOCITY.negate());
    return UnitSystem.SI().apply(criticalAngleDeg);
  }

  public Clip criticalSlipClip() {
    return Clips.interval(criticalSlip.negate(), criticalSlip);
  }

  public Clip dynamicCompensationBoundaryClip() {
    return Clips.interval(dynamicCompensationBoundary.negate(), dynamicCompensationBoundary);
  }

  public Clip latForceCompensationBoundaryClip() {
    return Clips.interval(latForceCompensationBoundary.negate(), latForceCompensationBoundary);
  }
}
