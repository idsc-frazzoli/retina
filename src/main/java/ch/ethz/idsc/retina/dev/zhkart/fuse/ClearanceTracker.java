// code by jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2ForwardAction;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Se2AxisYProject;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

public class ClearanceTracker {
  private final Scalar clearanceFrontMeter = SafetyConfig.GLOBAL.clearanceFrontMeter();
  private final Clip clip_Y;
  private final Tensor offset;
  private final Tensor u;
  private final Tensor collection = Tensors.empty();
  Scalar min;

  public ClearanceTracker(Scalar half, Scalar angle, Tensor se2_xya) {
    clip_Y = Clip.function(half.negate(), half); // TODO there is a small error as gokart turns
    Scalar speed = RealScalar.of(1.0); // assume unit speed // TODO use actual speed in logic
    u = Tensors.of(speed, RealScalar.ZERO, angle.multiply(speed)).unmodifiable();
    min = clearanceFrontMeter;
    this.offset = se2_xya.extract(0, 2).unmodifiable();
  }

  public void feed(Tensor local) {
    Tensor point = local.add(offset); // TODO not 100% accurate, should se2 map it
    Scalar t = Se2AxisYProject.of(u, point);
    // negate() in the next line helps to move point from front of gokart to y-axis of rear axle
    Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t.negate())));
    Tensor v = se2ForwardAction.apply(point);
    if (!Chop._08.allZero(v.Get(0)))
      System.err.println("did not map to rear axle");
    if (clip_Y.isInside(v.Get(1))) {// check y-coordinate of back projected point
      min = Min.of(min, t); // negate t again
      collection.append(point);
    }
  }

  public Optional<Tensor> violation() {
    if (Scalars.lessThan(min, clearanceFrontMeter))
      return Optional.of(Se2Utils.integrate_g0(u.multiply(min)));
    return Optional.empty();
  }

  public Tensor getPointsInViolation() {
    return collection.unmodifiable();
  }
}
