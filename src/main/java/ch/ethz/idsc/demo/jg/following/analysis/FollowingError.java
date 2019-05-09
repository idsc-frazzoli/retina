// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.util.Optional;

import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Round;

public class FollowingError implements ErrorInterface {
  private Tensor reference = Tensors.empty();
  private Tensor errors = Tensors.empty();
  // ---
  private Tensor times = Tensors.of( //
      Quantity.of(DoubleScalar.POSITIVE_INFINITY, SI.SECOND), //
      Quantity.of(DoubleScalar.NEGATIVE_INFINITY, SI.SECOND));

  public FollowingError() {
    System.err.println(String.format("WARN %s provides only an approximation of the actual error", this.getClass().getSimpleName())); // I told you
  }

  /** @param reference curve or trajectory */
  public void setReference(Tensor reference) {
    this.reference = Tensor.of(reference.stream().map(Extract2D.FUNCTION)).unmodifiable();
  }

  public Tensor getReference() {
    return reference.unmodifiable();
  }

  /** @param time [s]
   * @param pose of vehicle {x[m], y[m], angle} */
  public void insert(Scalar time, Tensor pose) {
    times.set(Min.of(times.Get(0), time), 0);
    times.set(Max.of(times.Get(1), time), 1);
    error(pose).ifPresent(errors::append);
  }

  /** @param pose of vehicle
   * @return approximation of error by distance to closest reference point */
  private Optional<Scalar> error(Tensor pose) {
    Tensor pose2D = Extract2D.FUNCTION.apply(pose);
    Tensor distances = Tensor.of(reference.stream().map(tensor -> tensor.subtract(pose2D)).map(Norm._2::ofVector));
    return distances.stream().reduce(Min::of).map(Tensor::Get);
  }

  @Override // from ErrorInterface
  public final Scalar averageError() {
    return Mean.of(errors).Get();
  }

  @Override // from ErrorInterface
  public final Scalar accumulatedError() {
    return errors.stream().map(Scalar.class::cast).reduce(Scalar::add).get();
  }

  @Override // from ErrorInterface
  public String getReport() {
    return "following error (" + this.getClass().getSimpleName() + ")\n" + //
        "\ttime:\t" + times.Get(0).map(Round._2) + " - " + times.Get(1).map(Round._2) + "\n" + //
        "\taverage error:\t" + averageError().map(Round._4) + "\n" + //
        "\taccumulated error:\t" + accumulatedError().map(Round._4);
  }
}
