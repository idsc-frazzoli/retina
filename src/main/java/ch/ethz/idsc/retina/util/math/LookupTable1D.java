// code by jph
package ch.ethz.idsc.retina.util.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Primitives;

public class LookupTable1D implements Serializable {
  private final float[] array;

  /** @param vector */
  public LookupTable1D(Tensor vector) {
    array = Primitives.toFloatArray(VectorQ.require(vector));
  }

  /** @param index
   * @return value in lookup table at given index */
  public float at(int index) {
    return array[index];
  }
}
