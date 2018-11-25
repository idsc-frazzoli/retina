// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.TrackInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class HomogenousTrack implements TrackInterface {
  public static final TrackInterface DRY_ROAD = new HomogenousTrack(FrictionCoefficients.TIRE_DRY_ROAD);
  // ---
  private final Scalar mu;

  public HomogenousTrack(Scalar mu) {
    this.mu = mu;
  }

  @Override
  public Scalar mu(Tensor x) {
    return mu;
  }
}
