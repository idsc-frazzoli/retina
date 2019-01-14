// concept and code by az
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.VectorFloatBlob;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** two instance of this class are used for left and right rear motors
 * @see RimoRateControllerDuo
 * 
 * Kp with unit "ARMS*rad^-1*s"
 * Ki with unit "ARMS*rad^-1"
 * Kawu with unit "" */
/* package */ class AntiWindupRimoRateController implements RimoRateController {
  static final Scalar DT = RimoSocket.INSTANCE.getPutPeriod();
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.RIMO_CONTROLLER_AW);
  private final RimoConfig rimoConfig;
  // ---
  /** pos error initially incorrect in the first iteration */
  private Scalar lastIPart = Quantity.of(0, NonSI.ARMS);

  /** @param rimoConfig for instance RimoConfig.GLOBAL */
  public AntiWindupRimoRateController(RimoConfig rimoConfig) {
    this.rimoConfig = rimoConfig;
  }

  @Override // from RimoRateController
  public Scalar iterate(final Scalar vel_error) {
    final Scalar pPart = vel_error.multiply(rimoConfig.Kp);
    // System.out.println("pPart=" + pPart);
    final Scalar iPart = vel_error.multiply(rimoConfig.Ki).multiply(DT).add(lastIPart);
    // System.out.println("iPart=" + iPart);
    // System.out.println("lastW=" + lastWindUp);
    // final Scalar TEMP_LWU = lastWindUp.multiply(RimoConfig.GLOBAL.Kawu);
    // System.out.println("teLwu=" + TEMP_LWU);
    final Scalar tor_value = pPart.add(iPart);
    // System.out.println(tor_value);
    final Scalar satTor_value = rimoConfig.torqueLimitClip().apply(tor_value); // actuator-saturation
    // update integral and anti-wind-up reset
    final Scalar windupPart = rimoConfig.Kawu.multiply(satTor_value.subtract(tor_value));
    // integral part plus anti-windup reset
    lastIPart = iPart.add(windupPart);
    // TODO preliminary for debugging: publish ctrl internals
    binaryBlobPublisher.accept(VectorFloatBlob.encode(Tensors.of( //
        vel_error, pPart, iPart, windupPart, tor_value, satTor_value)));
    return satTor_value;
  }

  @Override // from RimoRateController
  public void setWheelRate(Scalar vel_avg) {
    // ---
  }
}
