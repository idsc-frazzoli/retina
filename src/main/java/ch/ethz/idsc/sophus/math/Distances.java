// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Differences;

/** implementation taken from {@link Differences} */
// TODO JPH OWL 049 obsolete
public enum Distances {
  ;
  /** @param metric
   * @param tensor
   * @return */
  public static Tensor of(Metric<Tensor> metric, Tensor tensor) {
    int length = tensor.length();
    if (length <= 1)
      return Tensors.empty();
    List<Tensor> list = new ArrayList<>(length - 1);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    for (int index = 1; index < length; ++index) {
      Tensor next = iterator.next();
      list.add(metric.distance(prev, next));
      prev = next;
    }
    return Unprotect.using(list);
  }
}
