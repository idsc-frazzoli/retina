// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.io.Serializable;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldClip;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** Reference:
 * "Advanced Driver Assistance Systems on a Go-Kart" by A. Mosberger */
public class HapticSteerConfig implements Serializable {
  public static final HapticSteerConfig GLOBAL = AppResources.load(new HapticSteerConfig());
  /***************************************************/
  // TODO extract fields until tsuFactor to separate config: PowerSteerConfig
  /** values for PowerSteering */
  /** value to amplify the input in the PowerSteeringModule */
  public Boolean feedForward = true;
  // ---
  public Scalar tsuFilter = RealScalar.of(0.2);
  public Scalar velocityFilter = RealScalar.of(0.2);
  public Scalar latForceCompensation = Quantity.of(0.2, "SCT*s*m^-1");
  public Scalar latForceCompensationBoundary = Quantity.of(0.5, "SCT");
  /** tsuFactor in the interval [0, 1] */
  @FieldClip(min = "0", max = "1")
  public Scalar tsuFactor = RealScalar.of(0.8);
  /***************************************************/
  /** Constant Torque for Experiment */
  @FieldSubdivide(start = "-7/10[SCT]", end = "7/10[SCT]", intervals = 14)
  public Scalar constantTorque = Quantity.of(0, "SCT");
  /***************************************************/
  /** Values for Vibration Mode */
  public Scalar vibrationAmplitude = Quantity.of(0.4, "SCT");
  public Scalar vibrationFrequency = Quantity.of(12, SI.PER_SECOND);
  /***************************************************/
  /** LanekeepingFactor */
  public Scalar laneKeepingFactor = Quantity.of(-10.0, "SCT*SCE^-1");
  /** torque limit */
  public Scalar laneKeepingTorqueLimit = Quantity.of(0.8, "SCT");
  /** lane boundaries */
  public Scalar halfWidth = Quantity.of(0.5, SI.METER);
  /** planning period */
  public Scalar laneKeepingPeriod = Quantity.of(0.2, SI.SECOND);
  /***************************************************/
  @FieldSubdivide(start = "0.2 [s]", end = "2 [s]", intervals = 18)
  public Scalar prbs7BitWidth = Quantity.of(0.2, "s");
  /**
   * 
   */
  @FieldSubdivide(start = "0[SCT]", end = "1[SCT]", intervals = 20)
  public Scalar prbs7Amplitude = Quantity.of(RationalScalar.of(2, 10), "SCT");

  /***************************************************/
  // functions for power steering
  /** @return */
  public Clip latForceCompensationBoundaryClip() {
    return Clips.absolute(latForceCompensationBoundary);
  }

  /***************************************************/
  // functions for lane keeping
  /** @return */
  /* package */ Clip laneKeepingTorqueClip() {
    return Clips.absolute(laneKeepingTorqueLimit);
  }

  public Scalar laneKeeping(Scalar defect) {
    return laneKeepingTorqueClip().apply(defect.multiply(laneKeepingFactor));
  }
}
