// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

class FixedSpeedRimo extends PurePursuitRimo {
  static final Clip SAFETY = Clips.absolute(Quantity.of(10, SI.PER_SECOND));
  static final Scalar WHEEL_DIAMETER = Quantity.of(.3, SI.METER); // TODO replace placeholder
  // ---
  private final Scalar wheelCircumference = WHEEL_DIAMETER.multiply(Pi.VALUE);

  @Override // from PurePursuitRimo
  /* package */ void setSpeed(Scalar speed) {
    super.setSpeed(SAFETY.isInside(speed) ? Quantity.of(0, SI.PER_SECOND) : speedToRate(FixedSpeedParams.GLOBAL.speed));
  }

  /** @param speed [m*s^-1]
   * @return average wheel turning rate [s^-1] */
  // TODO might be better of in ChassisGeometry
  /* package */ Scalar speedToRate(Scalar speed) {
    return speed.divide(wheelCircumference);
  }
}
