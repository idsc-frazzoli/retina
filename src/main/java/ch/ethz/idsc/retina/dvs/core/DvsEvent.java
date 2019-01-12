// code by jph
package ch.ethz.idsc.retina.dvs.core;

import java.awt.Point;
import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.tensor.Tensors;

/** immutable */
// TODO long term, merge with DvsDavisEvent
public class DvsEvent implements Comparable<DvsEvent>, Serializable {
  /** @param time
   * @return */
  public static DvsEvent timeMark(long time) {
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

  public Point point() {
    return new Point(x, y);
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
  public int hashCode() {
    return Objects.hash(time_us, x, y, i);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof DvsEvent) {
      DvsEvent dvsEvent = (DvsEvent) object;
      return time_us == dvsEvent.time_us //
          && x == dvsEvent.x //
          && y == dvsEvent.y //
          && i == dvsEvent.i; //
    }
    return false;
  }

  @Override
  public String toString() {
    return Tensors.vector(time_us, x, y, i).toString();
  }
}
