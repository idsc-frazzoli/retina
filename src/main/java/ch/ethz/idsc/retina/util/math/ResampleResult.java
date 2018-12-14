// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;

public class ResampleResult {
  // TODO JPH magic constant specific to gokart !
  private static final Scalar OFFSET = DoubleScalar.of(0.75);
  // ---
  private final Interpolation interpolation;
  private final List<Tensor> list;
  private final int numel;

  public ResampleResult(Tensor points, List<Tensor> list) {
    interpolation = LinearInterpolation.of(points);
    this.list = list;
    numel = points.length();
  }

  public List<Tensor> getParameters() {
    return list;
  }

  public List<Tensor> getPoints() {
    return list.stream().map(vector -> vector.map(interpolation::at)).collect(Collectors.toList());
  }

  public List<Tensor> getPointsSpin(Scalar rate) {
    Clip clip = Clip.function(0, numel);
    List<Tensor> result = new ArrayList<>();
    for (Tensor vector : list) {
      Tensor entry = Tensors.empty();
      for (Tensor _param : vector) {
        Scalar param = (Scalar) _param;
        Tensor point = interpolation.at(param);
        // TODO rescale introduces error because it assumes regular sampling along the circle
        Scalar angle = clip.rescale(param).subtract(OFFSET).multiply(rate);
        Tensor matrix = RotationMatrix.of(angle);
        entry.append(matrix.dot(point));
      }
      result.add(entry);
    }
    return result;
  }
}
