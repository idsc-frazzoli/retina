// code by mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class WaypointPurePursuitModule extends PurePursuitModule {
  private Optional<Tensor> lookAhead = Optional.empty();

  @Override // from PurePursuitModule
  protected void protected_first() throws Exception {
    // ---
  }

  @Override // from PurePursuitModule
  protected void protected_last() {
    // ---
  }

  @Override // from PurePursuitModule
  protected Optional<Scalar> deriveHeading() {
    Optional<Scalar> ratio = getRatio();
    if (ratio.isPresent()) { // is look ahead beacon available?
      Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio.get());
      if (angleClip.isInside(angle)) // is look ahead beacon within steering range?
        return Optional.of(angle);
      System.err.println("beacon outside steering range");
    }
    return Optional.empty();
  }

  /* package */ Optional<Scalar> getRatio() {
    Optional<Tensor> lookAhead = this.lookAhead;
    if (lookAhead.isPresent())
      return PurePursuit.ratioPositiveX(lookAhead.get());
    System.err.println("no valid ratio");
    return Optional.empty();
  }

  /** @param lookAhead {x, y} unitless in go kart frame coordinates */
  public void setLookAhead(Optional<Tensor> lookAhead) {
    this.lookAhead = lookAhead;
  }
}
