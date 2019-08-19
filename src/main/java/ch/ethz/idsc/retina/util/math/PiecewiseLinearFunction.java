// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** evaluation for values outside the domain the nearest control point is returned */
public class PiecewiseLinearFunction implements ScalarTensorFunction {
  /** @param knots vector
   * @param values
   * @return */
  public static ScalarTensorFunction of(Tensor knots, Tensor values) {
    NavigableMap<Scalar, Tensor> navigableMap = new TreeMap<>();
    for (int index = 0; index < knots.length(); ++index)
      navigableMap.put(knots.Get(index), values.get(index));
    if (navigableMap.isEmpty())
      throw new RuntimeException();
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
      return ceiling.getValue().copy();
    if (Objects.isNull(ceiling))
      return floor.getValue().copy();
    Clip clip = Clips.interval(floor.getKey(), ceiling.getKey());
    return LinearInterpolation.of(Tensors.of(floor.getValue(), ceiling.getValue())).at(clip.rescale(scalar));
  }
}
