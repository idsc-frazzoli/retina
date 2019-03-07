// code by jph
package ch.ethz.idsc.owl.car.math;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.util.math.Se2AxisYProject;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;

public class CircleClearanceTracker implements ClearanceTracker, Serializable {
  private final Clip clip_Y;
  private final Clip clip_X;
  private final TensorUnaryOperator se2ForwardAction;
  private final Tensor u;
  private final TensorScalarFunction se2AxisYProject;
  // ---
  private Scalar min;

  /** Hint: unit speed in assumed units "m*s^-1"
   * the choice allows to compute distance to nearest obstacle
   * 
   * @param speed
   * @param half width along y-axis
   * @param angle steering
   * @param xya reference frame of sensor as 3-vector {px, py, angle}
   * @param clip_X */
  public CircleClearanceTracker(Scalar speed, Scalar half, Scalar angle, Tensor xya, Clip clip_X) {
    clip_Y = Clip.function(half.negate(), half); // TODO there is a small error as gokart turns
    this.clip_X = clip_X;
    u = Tensors.of(speed, speed.zero(), angle.multiply(speed)).unmodifiable();
    se2AxisYProject = Se2AxisYProject.of(u);
    min = clip_X.max();
    se2ForwardAction = new Se2Bijection(xya).forward();
  }

  @Override // from ClearanceTracker
  public boolean isObstructed(Tensor local) {
    Tensor point = se2ForwardAction.apply(local); // sensor to vehicle frame
    Scalar t = se2AxisYProject.apply(point);
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
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(u.multiply(t.negate()))).forward();
    Tensor v = se2ForwardAction.apply(point); // results in v.Get(0) == 0
    return clip_Y.isInside(v.Get(1));
  }

  public Optional<Tensor> violation() {
    if (Scalars.lessThan(min, clip_X.max())) // strictly less than
      return Optional.of(Se2CoveringExponential.INSTANCE.exp(u.multiply(min)));
    return Optional.empty();
  }

  @Override // from ClearanceTracker
  public Optional<Scalar> contact() {
    if (Scalars.lessThan(min, clip_X.max())) // strictly less than
      return Optional.of(min);
    return Optional.empty();
  }
}
