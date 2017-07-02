// code by jph
package ch.ethz.idsc.retina.core;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/** immutable */
public class DvsEvent implements Comparable<DvsEvent>, Serializable {
  /** @param time
   * @return */
  public static DvsEvent timeMark(int time) {
    return new DvsEvent(time, -1, -1, -1);
  }

  // ---
  public final long time_us;
  public final int x;
  public final int y;
  /** 1 or 0 */
  public final int i;

  public DvsEvent(long time_us, int x, int y, int i) {
    this.time_us = time_us;
    this.x = x;
    this.y = y;
    this.i = i;
  }

  public int signum() {
    return i == 1 ? 1 : -1;
  }

  public Scalar weight(Scalar factor) {
    return i == 1 ? factor : factor.negate();
  }

  @Override
  public int compareTo(DvsEvent dvsPoint) {
    int cmp = Long.compare(time_us, dvsPoint.time_us);
    if (cmp != 0)
      return cmp;
    cmp = Integer.compare(x, dvsPoint.x);
    if (cmp != 0)
      return cmp;
    cmp = Integer.compare(y, dvsPoint.y);
    if (cmp != 0)
      return cmp;
    return Integer.compare(i, dvsPoint.i);
  }

  @Override
  public String toString() {
    return Tensors.vector(time_us, x, y, i).toString();
  }
}
