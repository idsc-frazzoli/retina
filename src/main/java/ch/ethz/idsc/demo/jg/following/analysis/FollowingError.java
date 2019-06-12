// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Round;

public class FollowingError implements ErrorInterface {
  private static final Mod MOD = Mod.function(Pi.TWO);
  // ---
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
    this.reference = reference.unmodifiable();
  }

  public Tensor getReference() {
    return reference.unmodifiable();
  }

  /** @param time [s]
   * @param pose of vehicle {x[m], y[m], angle} */
  public void insert(Scalar time, Tensor pose) {
    times.set(Min.of(times.Get(0), time), 0);
    times.set(Max.of(times.Get(1), time), 1);
    errors.append(error(pose));
  }

  /** @param pose of vehicle
   * @return approximation of error by distance to closest reference point */
  private Tensor error(Tensor pose) {
    Tensor pose2D = Extract2D.FUNCTION.apply(pose);
    Tensor distances = Tensor.of(reference.stream().map(Extract2D.FUNCTION).map(tensor -> tensor.subtract(pose2D)).map(Norm._2::ofVector));
    int idx = ArgMin.of(distances);
    Scalar heading_error = Abs.of(MOD.apply(pose.Get(2)).subtract(MOD.apply(reference.get(idx).Get(2))));
    return Tensors.of(distances.get(idx), heading_error);
  }

  @Override // from ErrorInterface
  public final Optional<Tensor> averageError() {
    if (errors.length() > 0)
      return Optional.of(Mean.of(errors));
    return Optional.empty();
  }

  @Override // from ErrorInterface
  public final Optional<Tensor> accumulatedError() {
    return errors.stream().reduce(Tensor::add);
  }

  @Override // from ErrorInterface
  public Optional<String> getReport() {
    Optional<Tensor> averageError = averageError();
    if (averageError.isPresent()) {
      Tensor average = averageError.get().map(Round._4);
      Tensor accumulated = accumulatedError().get().map(Round._4);
      return Optional.of("following error (" + this.getClass().getSimpleName() + ")\n" + //
          "\ttime:\t" + times.Get(0).map(Round._2) + " - " + times.Get(1).map(Round._2) + "\n" + //
          "\taverage error:\tposition: " + average.Get(0) + ",\theading: " + average.Get(1) + "\n" + //
          "\taccumulated error:\tposition: " + accumulated.Get(0) + ",\theading: " + accumulated.Get(1));
    }
    return Optional.empty();
  }
}
