// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.calib.steer.ClipSteerMapping;
import ch.ethz.idsc.gokart.calib.steer.FittedSteerMapping;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldClip;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** parameters for PID controller of steering
 * 
 * there are 2 special units related to the manufacturer of the steering column:
 * "SCE" steer-column encoder
 * "SCT" steer-column torque */
public class SteerConfig {
  public static final SteerConfig GLOBAL = AppResources.load(new SteerConfig());
  /***************************************************/
  /** battery supply voltage ratings
   * 
   * ante 20180628: the steer battery was
   * Yuasa NP2.3-12 with 2Ah, lead acid, 6 cells
   * the range was 10.8[V] .. 13.0[V] for 1[s] confirmed with mac
   * 
   * post 20180628: the steer battery is replaced. the new model is
   * Lithium Polymer Akku: 45C/90C 4500mAh 11.1V 3S1P */
  public final Scalar voltageLo = Quantity.of(3.6 * 3, SI.VOLT); // 3.6 * 3 == 10.8[V]
  public final Scalar voltageHi = Quantity.of(4.2 * 3, SI.VOLT); // 4.2 * 3 == 12.9[V]
  // ---
  /** amplitude of signal during calibration procedure */
  @FieldClip(min = "0.5[SCT]", max = "1.5[SCT]")
  public final Scalar calibration = Quantity.of(1.5, "SCT");
  // ---
  /** .
   * 20190420 the steering column encoder values range in an interval
   * of width 1.49609[SCE] that is centered typically slightly offset
   * from zero. Example: on 20190401T115537 the interval was
   * {-0.75586[SCE], 0.740235[SCE]}.
   * 
   * the state interval width motivates the values
   * SteerColumnTracker.SOFT and SteerColumnTracker.HARD
   * 
   * the value columnMax refines the half width for autonomous steer
   * position control. the value columnMax is smaller than the
   * physically possible in order to avoid stress on the hardware. */
  public final Scalar columnMax = Quantity.of(SteerColumnTracker.SOFT / 2, SteerPutEvent.UNIT_ENCODER);
  /** 0.5 corresponds to 50% of torque limit */
  public final Scalar stepOfLimit = RealScalar.of(0.5);
  /** .
   * ante 20190509:
   * max turning rate per meter driven under the assumption of no slip
   * the numeric value was determined in an experiment documented in the report below
   * https://github.com/idsc-frazzoli/retina/files/1958724/20180429_minimum_turning_radius.pdf
   * The reciprocal gives the minimum turning radius to be approx. 2.45[m].
   * post 20190509:
   * https://github.com/idsc-frazzoli/retina/files/3160474/20190509_steering_turning_ratio.pdf */
  public final Scalar turningRatioMax = Quantity.of(0.45, SI.PER_METER);

  /***************************************************/
  /** @return voltage operating range of battery */
  public Clip operatingVoltageClip() {
    return Clips.interval(voltageLo, voltageHi);
  }

  /***************************************************/
  /** @return clip for quantities with unit "m^-1", limit of turning ratio in practice */
  public Clip getRatioLimit() {
    return Clips.absolute(turningRatioMax);
  }

  /** @return default steer mapping */
  public SteerMapping getSteerMapping() {
    SteerMapping steerMapping = FittedSteerMapping.instance();
    return ClipSteerMapping.wrap( //
        steerMapping, //
        Clips.absolute(steerMapping.getRatioFromSCE(columnMax)));
  }

  /* package */ Clip columnMaxClip() {
    return Clips.absolute(columnMax);
  }
}
