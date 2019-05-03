// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ abstract class OfflineFollowingError implements OfflineLogListener {
  private Tensor reference = Tensors.empty();
  private Tensor errors = Tensors.empty();
  // ---
  private Tensor times = Tensors.of(DoubleScalar.POSITIVE_INFINITY, DoubleScalar.NEGATIVE_INFINITY);

  /** @param reference curve or trajectory */
  protected void setReference(Tensor reference) {
    this.reference = Tensor.of(reference.stream().map(Extract2D.FUNCTION)).unmodifiable();
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    times.set(Min.of(times.Get(0), time), 0);
    times.set(Max.of(times.Get(1), time), 1);
    if (channel.equals(GokartLcmChannel.POSE_LIDAR) && Tensors.nonEmpty(reference)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      Tensor pose = gokartPoseEvent.getPose();
      error(pose).ifPresent(errors::append);
    }
  }

  /** @param pose of vehicle
   * @return approximation of error by distance to closest reference point */
  private Optional<Scalar> error(Tensor pose) {
    Tensor pose2D = Extract2D.FUNCTION.apply(pose);
    Tensor distances = Tensor.of(reference.stream().map(tensor -> tensor.subtract(pose2D)).map(Norm._2::ofVector));
    return distances.stream().reduce(Min::of).map(Tensor::Get);
  }

  public Scalar averageError() {
    return Mean.of(errors).Get();
  }

  public Scalar accumulatedError() {
    return errors.stream().map(Scalar.class::cast).reduce(Scalar::add).get();
  }

  public String report() {
    return "following error (" + this.getClass().getSimpleName() + ")\n" + //
        "\ttime:\t" + times.Get(0) + " - " + times.Get(1) + "\n" + //
        "\taverage error:\t" + averageError() + "\n" + //
        "\taccumulated error:\t" + accumulatedError();
  }
}
