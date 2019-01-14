// concept by jelavice
// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** two instance of this class are used for left and right rear motors
 * @see RimoRateControllerDuo
 * 
 * Kp with unit "ARMS*rad^-1*s"
 * Ki with unit "ARMS*rad^-1" */
/* package */ class SimpleRimoRateController implements RimoRateController {
  static final Scalar DT = RimoSocket.INSTANCE.getPutPeriod();
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.RIMO_CONTROLLER_PI);
  private final RimoConfig rimoConfig;
  // ---
  /** pos error initially incorrect in the first iteration */
  private Scalar lastVel_error = Quantity.of(0, SIDerived.RADIAN_PER_SECOND); // unit "rad*s^-1"
  private Scalar lastTor_value = Quantity.of(0, NonSI.ARMS); // unit "ARMS"

  public SimpleRimoRateController(RimoConfig rimoConfig) {
    this.rimoConfig = rimoConfig;
  }

  @Override // from RimoRateController
  public Scalar iterate(final Scalar vel_error) {
    final Scalar pPart = vel_error.subtract(lastVel_error).multiply(rimoConfig.Kp);
    final Scalar iPart = vel_error.multiply(rimoConfig.Ki).multiply(DT);
    final Scalar TEMP_LVE = lastVel_error; // TODO removal pending
    lastVel_error = vel_error;
    final Scalar TEMP_LTV = lastTor_value;
    final Scalar tor_value = lastTor_value.add(pPart).add(iPart);
    lastTor_value = rimoConfig.torqueLimitClip().apply(tor_value); // anti-windup
    // TODO preliminary for debugging: publish ctrl internals
    binaryBlobPublisher.accept(VectorFloatBlob.encode(Tensors.of( //
        vel_error, pPart, iPart, TEMP_LVE, TEMP_LTV, lastTor_value)));
    return lastTor_value;
  }

  @Override
  public void setWheelRate(Scalar vel_avg) {
    // ---
  }
}
