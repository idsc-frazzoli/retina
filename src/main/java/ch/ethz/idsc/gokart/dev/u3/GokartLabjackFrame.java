// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** investigation pending */
public final class GokartLabjackFrame implements ManualControlInterface {
  /** log file analysis shows that the throttle signal at AIN2
   * ranges from {-0.075455[V], 5.11837[V]}.
   * the lower bound is deliberately increased so that the lower bound
   * is insensitive to noise or minor activations of the throttle foot pedal. */
  private static final Clip THROTTLE_CLIP = Clips.interval( //
      Quantity.of(0.025, SI.VOLT), //
      Quantity.of(5.100, SI.VOLT));
  // ---
  private final Tensor allADC;

  public GokartLabjackFrame(ByteBuffer byteBuffer) {
    this(new LabjackAdcFrame(byteBuffer).allADC());
  }

  /** @param allADC vector of 5 quantities with unit [V] */
  /* package */ GokartLabjackFrame(Tensor allADC) {
    this.allADC = allADC;
  }

  /* package */ boolean isReversePressed() {
    return GokartLabjackAdc.REVERSE.isPressed(allADC);
  }

  /** @return value in the interval [0, 1] */
  private Scalar getThrottle() {
    return THROTTLE_CLIP.rescale(allADC.Get(GokartLabjackAdc.THROTTLE.ordinal()));
  }

  @Override // from ManualControlInterface
  public Scalar getSteerLeft() {
    return RealScalar.ZERO;
  }

  @Override // from ManualControlInterface
  public Scalar getBreakStrength() {
    return RealScalar.ZERO;
  }

  @Override // from ManualControlInterface
  public Scalar getAheadAverage() {
    Scalar scalar = getThrottle();
    return isReversePressed() //
        ? scalar.negate()
        : scalar;
  }

  @Override // from ManualControlInterface
  public Tensor getAheadPair_Unit() {
    return Tensors.of(RealScalar.ZERO, getAheadAverage());
  }

  @Override // from ManualControlInterface
  public boolean isAutonomousPressed() {
    return GokartLabjackAdc.AUTONOMOUS.isPressed(allADC);
  }

  @Override // from ManualControlInterface
  public boolean isResetPressed() {
    return GokartLabjackAdc.BOOST.isPressed(allADC);
  }

  @Override // from Object
  public String toString() {
    String b = isResetPressed() ? " B" : ""; // B for boost
    String r = isReversePressed() ? " R" : ""; // B for boost
    String a = isAutonomousPressed() ? " A" : "";
    return "t=" + getAheadAverage().map(Round._2) + b + r + a;
  }
}
