// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.Tensor;

public class PeriodicExtract {
  private final Tensor tensor;
  private final int length;

  public PeriodicExtract(Tensor tensor) {
    this.tensor = tensor;
    this.length = tensor.length();
  }

  /** @param index
   * @return copy of entry at location index modulus length */
  public Tensor get(int index) {
    return tensor.get(mod(index, length));
  }

  private static int mod(int index, int size) {
    int value = index % size;
    return value < 0 ? size + value : value;
  }
}
