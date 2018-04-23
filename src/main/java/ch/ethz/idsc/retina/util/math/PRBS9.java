// code from https://en.wikipedia.org/wiki/Pseudorandom_binary_sequence
// adapted by jph
package ch.ethz.idsc.retina.util.math;

import java.util.Iterator;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class PRBS9 implements Iterator<Scalar> {
  private final int seed;
  private int prev;
  private boolean hasNext = true;

  public PRBS9(int seed) {
    this.seed = seed;
    prev = seed;
  }

  public static Tensor sequence() {
    Tensor vector = Tensors.empty();
    final short seed = 0x01;
    short a = seed;
    while (true) {
      byte newbit = (byte) (((a >> 8) ^ (a >> 4)) & 1);
      vector.append(RealScalar.of(newbit));
      // ---
      a = (short) (((a << 1) | newbit) & 0x1ff); // 9-bit mask
      if (a == seed) {
        break;
      }
    }
    return vector;
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public Scalar next() {
    int newbit = ((prev >> 8) ^ (prev >> 4)) & 1;
    prev = (short) (((prev << 1) | newbit) & 0x1ff); // 9-bit mask
    hasNext = prev != seed;
    return RealScalar.of(newbit);
  }

  public static void main(String[] args) {
    {
      Tensor prbs9 = sequence();
      System.out.println(prbs9);
      System.out.println(prbs9.length());
    }
    {
      Tensor s = Tensors.empty();
      PRBS9 prbs9 = new PRBS9(0x01);
      while (prbs9.hasNext())
        s.append(prbs9.next());
      if (!s.equals(sequence()))
        throw new RuntimeException();
    }
  }
}
