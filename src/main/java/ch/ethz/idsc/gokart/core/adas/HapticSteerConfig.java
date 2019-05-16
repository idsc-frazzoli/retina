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
  /** value to amplify the input in the PowerSteeringModule */
  public Scalar staticCompensation = Quantity.of(0.7, "SCT*SCE^-1");
  public Scalar dynamicCompensation = Quantity.of(0, "SCT");
  public Scalar dynamicCompensationBoundary = Quantity.of(0.5, "SCT");
  public Scalar tsuFactor = Quantity.of(0.12, "SCT");
  public Scalar latForceCompensation = Quantity.of(0.0, "SCT*s*m^-1"); // so far 0.2
  public Scalar latForceCompensationBoundary = Quantity.of(2, "SCT"); // 5
  public Scalar velocityFilter = RealScalar.of(0.2);
  /** Constant Torque for Experiment */
  @FieldSubdivide(start = "-7/10[SCT]", end = "7/10[SCT]", intervals = 14)
  public Scalar constantTorque = Quantity.of(0, "SCT");
  /** Values for Vibration Mode */
  public Scalar vibrationAmplitude = Quantity.of(0.4, "SCT");
  public Scalar vibrationFrequency = Quantity.of(12, SI.PER_SECOND);
  public Scalar criticalSlip = RealScalar.of(0.2);
  /** Values for AntilockBrakeModule */
  /** access value via {@link #criticalAngle()} */
  public Scalar minSlip = Quantity.of(0.1, SI.PER_SECOND);
  public Scalar maxSlip = Quantity.of(0.25, SI.PER_SECOND);
  public Scalar fullBraking = RealScalar.of(0.85);
  public Scalar incrBraking = RealScalar.of(0.05);
  public Scalar criticalAngle = Quantity.of(12, NonSI.DEGREE_ANGLE);
  public Scalar absFrequency = RealScalar.of(5);
  public Scalar absAmplitude = RealScalar.of(0.2);
  public double absDuration = 1;

  /***************************************************/
  public Scalar criticalAngle() {
    return UnitSystem.SI().apply(criticalAngle);
  }

  public Clip criticalSlipClip() {
    return Clips.absolute(criticalSlip);
  }

  public Clip dynamicCompensationBoundaryClip() {
    return Clips.absolute(dynamicCompensationBoundary);
  }

  public Clip latForceCompensationBoundaryClip() {
    return Clips.absolute(latForceCompensationBoundary);
  }

  public Clip slipClip() {
    return Clips.interval(minSlip, maxSlip);
  }
}
