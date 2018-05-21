// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;

public class PiecewiseLinearFunction implements ScalarTensorFunction {
  /** @param knots vector
   * @param cp
   * @return */
  public static ScalarTensorFunction of(Tensor knots, Tensor cp) {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 0; index < knots.length(); ++index)
      navigableMap.put(knots.Get(index), cp.get(index));
    return new PiecewiseLinearFunction(navigableMap);
  }

  // ---
  private final NavigableMap<Scalar, Tensor> navigableMap;

  private PiecewiseLinearFunction(NavigableMap<Scalar, Tensor> navigableMap) {
    this.navigableMap = navigableMap;
  }

  @Override
  public Tensor apply(Scalar scalar) {
    Entry<Scalar, Tensor> floor = navigableMap.floorEntry(scalar);
    Entry<Scalar, Tensor> ceiling = navigableMap.ceilingEntry(scalar);
    if (Objects.isNull(floor))
      return ceiling.getValue();
    if (Objects.isNull(ceiling))
      return floor.getValue();
    Clip clip = Clip.function(floor.getKey(), ceiling.getKey());
    Scalar lambda = clip.rescale(scalar);
    Interpolation interpolation = LinearInterpolation.of(Tensors.of(floor.getValue(), ceiling.getValue()));
    return interpolation.at(lambda);
  }
}
