// code from https://en.wikipedia.org/wiki/Pseudorandom_binary_sequence
// adapted by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum PRBS7 {
  ;
  public static Tensor sequence() {
    Tensor vector = Tensors.empty();
    final byte seed = 0x01;
    byte a = seed;
    while (true) {
      byte newbit = (byte) (((a >> 6) ^ (a >> 5)) & 1);
      vector.append(RealScalar.of(newbit));
      // ---
      a = (byte) (((a << 1) | newbit) & 0x7f); // 7-bit mask
      if (a == seed) {
        break;
      }
    }
    return vector;
  }
}
