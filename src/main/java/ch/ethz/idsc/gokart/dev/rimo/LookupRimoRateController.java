// concept by jelavice
// code by mh
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** two instance of this class are used for left and right rear motors
 * @see RimoRateControllerDuo
 * 
 * Kp with unit "ARMS*rad^-1*s"
 * Ki with unit "ARMS*rad^-1" */
/* package */ class LookupRimoRateController implements RimoRateController {
  static final Scalar DT = RimoSocket.INSTANCE.getPutPeriod();
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.RIMO_CONTROLLER_LT);
  private final PowerLookupTable lookupTable = PowerLookupTable.getInstance();
  private final RimoConfig rimoConfig;

  // ---
  /** pos error initially incorrect in the first iteration */
  // private Scalar lastVel_error = Quantity.of(0, SIDerived.RADIAN_PER_SECOND); // unit "rad*s^-1"
  public LookupRimoRateController(RimoConfig rimoConfig) {
    this.rimoConfig = rimoConfig;
  }

  // public because of testing
  private Scalar integral = Quantity.of(0, SI.METER);
  /** gokart velocity */
  private Scalar velocity = Quantity.of(0, SI.VELOCITY);

  @Override // from RimoRateController
  public Scalar iterate(final Scalar vel_error) {
    final Scalar tangentVelError = ChassisGeometry.GLOBAL.tireRadiusRear.multiply(vel_error);
    final Scalar pPart = tangentVelError.multiply(rimoConfig.lKp);
    final Scalar iPart = integral.multiply(rimoConfig.lKi);
    final Scalar acc_value = pPart.add(iPart);
    final Scalar currentValue = lookupTable.getNeededCurrent(acc_value, velocity);
    // get min and max available
    // System.out.println("currentValue=" + currentValue);
    Tensor minmax = lookupTable.getMinMaxAcceleration(velocity);
    // anti windup
    if (Scalars.lessThan(minmax.Get(0), acc_value) && Scalars.lessThan(acc_value, minmax.Get(1))) {
      // update integral
      integral = integral.add(tangentVelError.multiply(DT));
    }
    binaryBlobPublisher.accept(VectorFloatBlob.encode(Tensors.of( //
        vel_error, pPart, iPart, integral, velocity)));
    return currentValue;
  }

  @Override // from RimoRateController
  public void setWheelRate(Scalar vel_avg) {
    velocity = vel_avg.multiply(ChassisGeometry.GLOBAL.tireRadiusRear);
  }
}
