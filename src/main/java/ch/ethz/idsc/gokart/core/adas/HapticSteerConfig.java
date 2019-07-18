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

public class HapticSteerConfig implements Serializable {
  public static final HapticSteerConfig GLOBAL = AppResources.load(new HapticSteerConfig());
  /***************************************************/
  /** value to amplify the input in the PowerSteeringModule */
  public Boolean feedForward = true;
  // ---
  public Scalar velocityFilter = RealScalar.of(0.2);
  public Scalar latForceCompensation = Quantity.of(0.2, "SCT*s*m^-1");
  public Scalar latForceCompensationBoundary = Quantity.of(0.5, "SCT");
  /** tsuFactor in the interval [0, 1] */
  public Scalar tsuFactor = RealScalar.of(0.8);
  public Boolean printPower = false;
  /** Constant Torque for Experiment */
  @FieldSubdivide(start = "-7/10[SCT]", end = "7/10[SCT]", intervals = 14)
  public Scalar constantTorque = Quantity.of(0, "SCT");
  /** Values for Vibration Mode */
  public Scalar vibrationAmplitude = Quantity.of(0.4, "SCT");
  public Scalar vibrationFrequency = Quantity.of(12, SI.PER_SECOND);
  public Scalar criticalSlip = RealScalar.of(0.2);
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
  public Scalar absFrequency = RealScalar.of(5);
  public Scalar absAmplitude = RealScalar.of(0.2);
  public double absDuration = 1;
  /** set velocity for a full stop with or without anti-lock braking */
  @FieldSubdivide(start = "5.75[m*s^-1]", end = "8.5[m*s^-1]", intervals = 11)
  public Scalar setVel = Quantity.of(6.5, SI.VELOCITY);
  /** LanekeepingFactor */
  public Scalar laneKeepingFactor = Quantity.of(-5.0, "SCT*SCE^-1");
  /** torque limit */
  public Scalar laneKeepingTorqueLimit = Quantity.of(0.5, "SCT");
  public Boolean printLaneInfo = false;

  /***************************************************/
  // functions for anti-lock brake
  public Scalar criticalAngle() {
    return UnitSystem.SI().apply(criticalAngle);
  }

  public Clip slipClip() {
    return Clips.interval(minSlip, maxSlip);
  }

  /***************************************************/
  // functions for power steering
  /** @return */
  public Clip latForceCompensationBoundaryClip() {
    return Clips.absolute(latForceCompensationBoundary);
  }

  /***************************************************/
  // functions for lane keeping
  /** @return */
  public Clip laneKeepingTorqueClip() {
    return Clips.absolute(laneKeepingTorqueLimit);
  }

  public Scalar laneKeeping(Scalar defect) {
    return laneKeepingTorqueClip().apply(defect.multiply(laneKeepingFactor));
  }
}
