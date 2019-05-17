// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** investigation pending */
public final class GokartLabjackFrame implements ManualControlInterface {
  public static final Tensor ARRAY_PASSIVE = Array.zeros(5).unmodifiable();
  public static final ManualControlInterface PASSIVE = //
      new GokartLabjackFrame(ARRAY_PASSIVE);
  /** 0.3[V] when not pressed, 2.45[V]
   * 1.1[V] when not pressed, 5.11[V] */
  private static final Scalar BOOST_BUTTON_TRESHOLD = RealScalar.of(4.5f);
  private static final int BOOST_BUTTON_INDEX = 0;
  /** 1.1[V] when not pressed, 5.11[V] */
  private static final Scalar REVERSE_BUTTON_TRESHOLD = RealScalar.of(4.5f);
  private static final int REVERSE_BUTTON_INDEX = 1;
  /** log file analysis shows that the throttle signal at AIN2
   * ranges from {-0.075455[V], 5.11837[V]}.
   * the lower bound is deliberately increased so that the lower bound
   * is insensitive to noise or minor activations of the throttle foot pedal. */
  private static final Clip THROTTLE_CLIP = Clips.interval(0.1, 5);
  private static final int THROTTLE_INDEX = 2;
  /**
   * 
   */
  private static final Scalar AUTONOMOUS_BUTTON_TRESHOLD = RealScalar.of(7f);
  private static final int AUTONOMOUS_BUTTON_INDEX = 3;
  // ---
  private final Tensor frame;

  public GokartLabjackFrame(ByteBuffer byteBuffer) {
    this(new LabjackAdcFrame(byteBuffer).asVector());
  }

  GokartLabjackFrame(Tensor frame) {
    this.frame = frame;
  }

  /* package */ boolean isReversePressed() {
    return Scalars.lessThan(REVERSE_BUTTON_TRESHOLD, frame.Get(REVERSE_BUTTON_INDEX));
  }

  /** @return value in the interval [0, 1] */
  private Scalar getThrottle() {
    return THROTTLE_CLIP.rescale(frame.Get(THROTTLE_INDEX));
  }

  @Override
  public Scalar getSteerLeft() {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar getBreakStrength() {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar getAheadAverage() {
    Scalar scalar = getThrottle();
    return isReversePressed() //
        ? scalar.negate()
        : scalar;
  }

  @Override
  public Tensor getAheadPair_Unit() {
    return Tensors.of(RealScalar.ZERO, getAheadAverage());
  }

  @Override
  public boolean isAutonomousPressed() {
    return Scalars.lessThan(AUTONOMOUS_BUTTON_TRESHOLD, frame.Get(AUTONOMOUS_BUTTON_INDEX));
  }

  @Override
  public boolean isResetPressed() {
    return Scalars.lessThan(BOOST_BUTTON_TRESHOLD, frame.Get(BOOST_BUTTON_INDEX));
  }

  @Override
  public String toString() {
    String b = isResetPressed() ? " B" : ""; // B for boost
    String r = isReversePressed() ? " R" : ""; // B for boost
    String a = isAutonomousPressed() ? " A" : "";
    return "t=" + getAheadAverage().map(Round._2) + b + r + a;
  }
}
