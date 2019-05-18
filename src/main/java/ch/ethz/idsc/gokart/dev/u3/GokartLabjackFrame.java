// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** investigation pending */
public final class GokartLabjackFrame implements ManualControlInterface {
  /** 0.3[V] when not pressed, 2.45[V]
   * 1.1[V] when not pressed, 5.11[V] */
  private static final Scalar BOOST_BUTTON_TRESHOLD = Quantity.of(4.5, SI.VOLT);
  private static final int BOOST_BUTTON_INDEX = 0;
  /** 1.1[V] when not pressed, 5.11[V] */
  private static final Scalar REVERSE_BUTTON_TRESHOLD = Quantity.of(4.5, SI.VOLT);
  private static final int REVERSE_BUTTON_INDEX = 1;
  /** log file analysis shows that the throttle signal at AIN2
   * ranges from {-0.075455[V], 5.11837[V]}.
   * the lower bound is deliberately increased so that the lower bound
   * is insensitive to noise or minor activations of the throttle foot pedal. */
  private static final Clip THROTTLE_CLIP = Clips.interval(Quantity.of(0.1, SI.VOLT), Quantity.of(5, SI.VOLT));
  private static final int THROTTLE_INDEX = 2;
  /**
   * 
   */
  private static final Scalar AUTONOMOUS_BUTTON_TRESHOLD = Quantity.of(7, SI.VOLT);
  private static final int AUTONOMOUS_BUTTON_INDEX = 3;
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
    return Scalars.lessThan(REVERSE_BUTTON_TRESHOLD, allADC.Get(REVERSE_BUTTON_INDEX));
  }

  /** @return value in the interval [0, 1] */
  private Scalar getThrottle() {
    return THROTTLE_CLIP.rescale(allADC.Get(THROTTLE_INDEX));
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
    return Scalars.lessThan(AUTONOMOUS_BUTTON_TRESHOLD, allADC.Get(AUTONOMOUS_BUTTON_INDEX));
  }

  @Override // from ManualControlInterface
  public boolean isResetPressed() {
    return Scalars.lessThan(BOOST_BUTTON_TRESHOLD, allADC.Get(BOOST_BUTTON_INDEX));
  }

  @Override // from Object
  public String toString() {
    String b = isResetPressed() ? " B" : ""; // B for boost
    String r = isReversePressed() ? " R" : ""; // B for boost
    String a = isAutonomousPressed() ? " A" : "";
    return "t=" + getAheadAverage().map(Round._2) + b + r + a;
  }
}
