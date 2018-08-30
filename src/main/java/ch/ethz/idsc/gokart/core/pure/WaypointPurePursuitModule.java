// code by mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class WaypointPurePursuitModule extends PurePursuitModule {
  private Optional<Tensor> lookAhead = Optional.empty();

  @Override // from PurePursuitModule
  Optional<Scalar> getRatio(Tensor pose, boolean isForward) {
    Optional<Tensor> lookAhead = this.lookAhead;
    if (lookAhead.isPresent() && isForward) {
      TensorUnaryOperator toLocal = new Se2Bijection(GokartPoseHelper.toUnitless(pose)).inverse();
      Tensor tensor = toLocal.apply(lookAhead.get());
      Optional<Scalar> ratio = PurePursuit.ratioPositiveX(tensor);
      System.out.println(ratio);
      return ratio;
    }
    System.err.println("no valid ratio");
    return Optional.empty();
  }

  // TODO use quantity and meters
  /** @param lookAhead {x, y} in world frame coordinates */
  public void setLookAhead(Optional<Tensor> lookAhead) {
    this.lookAhead = lookAhead;
  }
}
