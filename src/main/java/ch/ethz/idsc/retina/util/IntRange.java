// code by jph
package ch.ethz.idsc.retina.util;

import java.io.Serializable;

import ch.ethz.idsc.tensor.sca.Clip;

/** Hint: the use of {@link Clip} is preferred */
public final class IntRange implements Serializable {
  /** inclusive */
  public final int min;
  /** exclusive */
  public final int max;

  public IntRange(int min, int max) {
    if (max < min)
      throw new RuntimeException("min=" + min + " gt max=" + max);
    this.min = min;
    this.max = max;
  }

  public int getWidth() {
    return max - min;
  }

  @Override
  public final String toString() {
    return String.format("[%d,%d)", min, max);
  }

  @Override
  public final boolean equals(Object object) {
    IntRange myIntRange = (IntRange) object;
    return (min == myIntRange.min && max == myIntRange.max);
  }

  @Override
  public final int hashCode() {
    return min + max;
  }
}
