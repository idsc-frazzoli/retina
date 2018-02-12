// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

public class ResampleResult {
  private final Interpolation interpolation;
  private final List<Tensor> list;

  public ResampleResult(Tensor points, List<Tensor> list) {
    interpolation = LinearInterpolation.of(points);
    this.list = list;
  }

  public List<Tensor> getParameters() {
    return list;
  }

  public List<Tensor> getPoints() {
    return list.stream() //
        .map(ret -> Tensor.of(ret.stream().map(Tensors::of).map(interpolation::get))) //
        .collect(Collectors.toList());
  }
}
