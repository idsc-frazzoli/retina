// code by jph
package ch.ethz.idsc.retina.util.math;

import java.nio.FloatBuffer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class LookupTable1D {
  private final float[] array;

  /** @param vector */
  public LookupTable1D(Tensor vector) {
    array = new float[vector.length()];
    FloatBuffer floatBuffer = FloatBuffer.wrap(array);
    vector.stream() //
        .map(Scalar.class::cast) //
        .map(Scalar::number) //
        .forEach(n -> floatBuffer.put(n.floatValue()));
  }

  /** @param index
   * @return value in lookup table at given index */
  public float at(int index) {
    return array[index];
  }
}
