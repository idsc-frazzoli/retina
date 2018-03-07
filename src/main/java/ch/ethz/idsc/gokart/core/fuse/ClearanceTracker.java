// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
  // TODO design: make class more generic by passing all clearance config as parameters
  private final Scalar clearanceFrontMeter = SafetyConfig.GLOBAL.clearanceFrontMeter();
  private final Clip clip_Y;
  private final Clip clip_X;
  private final Se2ForwardAction se2ForwardAction;
  private final Tensor u;
  private final Collection<Tensor> collection = new LinkedList<>();
  private Scalar min;

  /** @param half width along y-axis
   * @param angle steering
   * @param xya reference frame of sensor as 3-vector {px, py, angle} */
  public ClearanceTracker(Scalar half, Scalar angle, Tensor xya) {
    clip_Y = Clip.function(half.negate(), half); // TODO there is a small error as gokart turns
    Scalar speed = RealScalar.of(1.0); // assume unit speed // TODO use actual speed in logic
    u = Tensors.of(speed, RealScalar.ZERO, angle.multiply(speed)).unmodifiable();
    min = clearanceFrontMeter;
    se2ForwardAction = new Se2ForwardAction(xya);
    clip_X = Clip.function(RealScalar.of(0.2), clearanceFrontMeter); // TODO magic const 0.2
  }

  // TODO refactor necessary due to redundancy
  /** @param local coordinates of obstacle in sensor reference frame
   * @return whether given point is an obstruction */
  public boolean probe(Tensor local) {
    Tensor point = se2ForwardAction.apply(local);
    Scalar t = Se2AxisYProject.of(u, point);
    // negate() in the next line helps to move point from front of gokart to y-axis of rear axle
    Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t.negate())));
    Tensor v = se2ForwardAction.apply(point);
    if (!Chop._08.allZero(v.Get(0)))
      System.err.println("did not map to rear axle(2)");
    if (clip_Y.isInside(v.Get(1)))
      return clip_X.isInside(t);
    // return Scalars.lessThan(t, clearanceFrontMeter);
    return false;
  }

  /** @param local coordinates of obstacle in sensor reference frame */
  public void feed(Tensor local) {
    Tensor point = se2ForwardAction.apply(local);
    Scalar t = Se2AxisYProject.of(u, point);
    // negate() in the next line helps to move point from front of gokart to y-axis of rear axle
    Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t.negate())));
    Tensor v = se2ForwardAction.apply(point);
    if (!Chop._08.allZero(v.Get(0)))
      System.err.println("did not map to rear axle");
    if (clip_Y.isInside(v.Get(1))) { // check y-coordinate of back projected point
      min = Min.of(min, t); // negate t again
      collection.add(point);
    }
  }

  /** @return closest of all obstructing points, or empty */
  public Optional<Tensor> violation() {
    if (Scalars.lessThan(min, clearanceFrontMeter))
      return Optional.of(Se2Utils.integrate_g0(u.multiply(min)));
    return Optional.empty();
  }

  /** @return unmodifiable collection with points that were determined to be in path */
  public Collection<Tensor> getPointsInViolation() {
    return Collections.unmodifiableCollection(collection);
  }
}
