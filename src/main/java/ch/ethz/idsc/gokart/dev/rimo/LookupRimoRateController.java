// concept by jelavice
// code by mh
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/** two instance of this class are used for left and right rear motors
 * 
 * lKp with unit "s^-1"
 * lKi with unit "s^-2" */
/* package */ class LookupRimoRateController implements RimoRateController {
  static final Scalar DT = RimoSocket.INSTANCE.getPutPeriod();
  // ---
  private final PowerLookupTable lookupTable = PowerLookupTable.getInstance();
  private final RimoConfig rimoConfig;

  // ---
  /** pos error initially incorrect in the first iteration */
  public LookupRimoRateController(RimoConfig rimoConfig) {
    this.rimoConfig = rimoConfig;
  }

  private Scalar integral = Quantity.of(0, SI.METER);
  /** gokart velocity */
  private Scalar velocity = Quantity.of(0, SI.VELOCITY);

  @Override // from RimoRateController
  public Scalar iterate(final Scalar vel_error) {
    final Scalar tangentVelError = RimoTireConfiguration._REAR.radius().multiply(vel_error);
    final Scalar pPart = tangentVelError.multiply(rimoConfig.lKp);
    final Scalar iPart = integral.multiply(rimoConfig.lKi);
    final Scalar acc_value = pPart.add(iPart);
    final Scalar currentValue = lookupTable.getNeededCurrent(acc_value, velocity);
    Tensor minmax = lookupTable.getMinMaxAcceleration(velocity); // get min and max available
    // TODO JPH check if can do more precise clipping
    if (Scalars.lessThan(minmax.Get(0), acc_value) && //
        Scalars.lessThan(acc_value, minmax.Get(1))) // anti windup
      integral = integral.add(tangentVelError.multiply(DT)); // update integral
    return currentValue;
  }

  @Override // from RimoRateController
  public void setWheelRate(Scalar vel_avg) {
    velocity = vel_avg.multiply(RimoTireConfiguration._REAR.radius());
  }
}
