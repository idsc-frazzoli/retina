// code by jph
package ch.ethz.idsc.owl.car.math;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2ForwardAction;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Se2AxisYProject;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;

public class CircleClearanceTracker implements ClearanceTracker, Serializable {
  /** unit speed in assumed units "m*s^-1"
   * the choice allows to compute distance to nearest obstacle */
  private static final Scalar UNIT_SPEED = DoubleScalar.of(1);
  // ---
  private final Clip clip_Y;
  private final Clip clip_X;
  private final Se2ForwardAction se2ForwardAction;
  private final Tensor u;
  // ---
  private Scalar min;

  /** @param half width along y-axis
   * @param angle steering
   * @param xya reference frame of sensor as 3-vector {px, py, angle}
   * @param clip_X */
  public CircleClearanceTracker(Scalar half, Scalar angle, Tensor xya, Clip clip_X) {
    clip_Y = Clip.function(half.negate(), half); // TODO there is a small error as gokart turns
    this.clip_X = clip_X;
    Scalar speed = UNIT_SPEED; // assume unit speed // use actual speed in logic
    u = Tensors.of(speed, RealScalar.ZERO, angle.multiply(speed)).unmodifiable();
    min = clip_X.max();
    se2ForwardAction = new Se2ForwardAction(xya);
  }

  @Override // from ClearanceTracker
  public boolean isObstructed(Tensor local) {
    Tensor point = se2ForwardAction.apply(local); // sensor to vehicle frame
    Scalar t = Se2AxisYProject.of(u, point);
    boolean status = clip_X.isInside(t) && probeY(point, t);
    if (status)
      min = Min.of(min, t);
    return status;
  }

  /** @param point
   * @param t
   * @return */
  private boolean probeY(Tensor point, Scalar t) {
    // negate() in the next line helps to move point from front of gokart to y-axis of rear axle
    Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t.negate())));
    Tensor v = se2ForwardAction.apply(point); // results in v.Get(0) == 0
    return clip_Y.isInside(v.Get(1));
  }

  public Optional<Tensor> violation() {
    if (Scalars.lessThan(min, clip_X.max())) // strictly less than
      return Optional.of(Se2Utils.integrate_g0(u.multiply(min)));
    return Optional.empty();
  }

  @Override // from ClearanceTracker
  public Optional<Scalar> contact() {
    if (Scalars.lessThan(min, clip_X.max())) // strictly less than
      return Optional.of(min);
    return Optional.empty();
  }
}
