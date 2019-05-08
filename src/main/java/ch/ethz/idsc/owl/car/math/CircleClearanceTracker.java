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
import ch.ethz.idsc.tensor.sca.Clips;

public class CircleClearanceTracker implements ClearanceTracker, Serializable {
  private final Clip clip_Y;
  private final Clip clip_X;
  private final TensorUnaryOperator se2ForwardAction;
  private final Tensor u;
  private final TensorScalarFunction se2AxisYProject;
  // ---
  /** with unit [s] */
  private Scalar min_time;

  /** Hint: unit speed in assumed units "m*s^-1"
   * the choice allows to compute distance to nearest obstacle
   * 
   * @param speed [m*s^-1]
   * @param half width along y-axis [m]
   * @param ratio steering [m^-1]
   * @param xya reference frame of sensor as 3-vector {px, py, angle}
   * @param clip_time with min and max with unit [s] */
  public CircleClearanceTracker(Scalar speed, Scalar half, Scalar ratio, Tensor xya, Clip clip_time) {
    clip_Y = Clips.interval(half.negate(), half); // TODO JPH there is a small error as gokart turns
    this.clip_X = clip_time;
    // [m*s^-1], [m*s^-1], [s^-1]
    u = Tensors.of(speed, speed.zero(), ratio.multiply(speed)).unmodifiable();
    se2AxisYProject = Se2AxisYProject.of(u);
    min_time = clip_time.max();
    se2ForwardAction = new Se2Bijection(xya).forward();
  }

  @Override // from ClearanceTracker
  public boolean isObstructed(Tensor local) {
    Tensor point = se2ForwardAction.apply(local); // sensor to vehicle frame
    Scalar time = se2AxisYProject.apply(point); // [s]
    boolean status = clip_X.isInside(time) && probeY(point, time);
    if (status)
      min_time = Min.of(min_time, time);
    return status;
  }

  /** @param point
   * @param time
   * @return */
  private boolean probeY(Tensor point, Scalar time) {
    // negate() in the next line helps to move point from front of gokart to y-axis of rear axle
    TensorUnaryOperator se2ForwardAction = //
        new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(u.multiply(time.negate()))).forward();
    Tensor v = se2ForwardAction.apply(point); // results in v.Get(0) == 0
    return clip_Y.isInside(v.Get(1));
  }

  /** @return {x[m], y[m], angle} */
  public Optional<Tensor> violation() {
    if (Scalars.lessThan(min_time, clip_X.max())) // strictly less than
      return Optional.of(Se2CoveringExponential.INSTANCE.exp(u.multiply(min_time)));
    return Optional.empty();
  }

  @Override // from ClearanceTracker
  public Optional<Scalar> contact() {
    if (Scalars.lessThan(min_time, clip_X.max())) // strictly less than
      return Optional.of(min_time);
    return Optional.empty();
  }
}
