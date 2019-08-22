// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.lie.so2.So2Metric;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Round;

public class FollowingError implements ErrorInterface {
  private Tensor reference = Tensors.empty();
  private Tensor errors = Tensors.empty();
  // ---
  private Scalar startTime = Quantity.of(DoubleScalar.POSITIVE_INFINITY, SI.SECOND);
  private Scalar endTime = Quantity.of(DoubleScalar.NEGATIVE_INFINITY, SI.SECOND);

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
    if (!Tensors.isEmpty(reference)) {
      startTime = Min.of(startTime, time);
      endTime = Max.of(endTime, time);
      errors.append(error(pose));
    }
  }

  /** @param pose of vehicle
   * @return approximation of error by distance to closest reference point */
  private Tensor error(Tensor pose) {
    Tensor pose2D = Extract2D.FUNCTION.apply(pose);
    Tensor distances = Tensor.of(reference.stream().map(Extract2D.FUNCTION).map(tensor -> tensor.subtract(pose2D)).map(Norm._2::ofVector));
    int idx = ArgMin.of(distances);
    return Tensors.of(distances.get(idx), So2Metric.INSTANCE.distance(pose.get(2), reference.get(idx, 2)));
  }

  @Override // from ErrorInterface
  public final Optional<Tensor> averageError() {
    if (errors.length() > 0)
      return Optional.of(Mean.of(errors));
    return Optional.empty();
  }

  @Override // from ErrorInterface
  public final Optional<Tensor> maximumError() {
    if (errors.length() > 0)
      return Optional.of(Tensor.of(Transpose.of(errors).stream().map(t -> t.stream().reduce(Max::of).get())));
    return Optional.empty();
  }

  @Override // from ErrorInterface
  public final Optional<Tensor> accumulatedError() {
    return errors.stream().reduce(Tensor::add);
  }

  @Override // from ErrorInterface
  public Optional<String> getReport() {
    String report = "";
    Optional<Tensor> optional = averageError();
    if (optional.isPresent()) {
      Tensor avg = optional.get();
      report += "\taverage error:\tposition: " + avg.Get(0) + ",\theading: " + avg.Get(1) + "\n";
    }
    optional = maximumError();
    if (optional.isPresent()) {
      Tensor max = optional.get();
      report += "\tmaximum error:\tposition: " + max.Get(0) + ",\theading: " + max.Get(1) + "\n";
    }
    optional = accumulatedError();
    if (optional.isPresent()) {
      Tensor acc = optional.get();
      report += "\tsummed error:\tposition: " + acc.Get(0) + ",\theading: " + acc.Get(1);
    }
    if (report.length() > 0)
      return Optional.of("following error (" + this.getClass().getSimpleName() + ")\n" + //
          "\ttime:\t" + Round._2.apply(startTime) + " - " + Round._2.apply(endTime) + "\n" + //
          report);
    else
      return Optional.empty();
  }
}
