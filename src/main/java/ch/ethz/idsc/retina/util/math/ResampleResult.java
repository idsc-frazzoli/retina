// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class ResampleResult {
  private final Interpolation interpolation;
  private final List<Tensor> list;
  private final Clip clip;

  /** @param points
   * @param list */
  public ResampleResult(Tensor points, List<Tensor> list) {
    interpolation = LinearInterpolation.of(points);
    this.list = list;
    clip = Clips.interval(0, points.length());
  }

  public List<Tensor> getParameters() {
    return list;
  }

  public List<Tensor> getPoints() {
    return list.stream() //
        .map(vector -> vector.map(interpolation::at)) //
        .collect(Collectors.toList());
  }

  /** @param relativeZero in the interval [0, 1]
   * @param rate unitless
   * @return */
  public List<Tensor> getPointsSpin(Scalar relativeZero, Scalar rate) {
    // TODO JPH rescale introduces error because it assumes regular sampling along the circle
    return list.stream() //
        .map(vector -> vector.map(param -> RotationMatrix.of(clip.rescale(param).subtract(relativeZero).multiply(rate)).dot(interpolation.at(param)))) //
        .collect(Collectors.toList());
  }
}
