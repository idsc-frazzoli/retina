// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import ch.ethz.idsc.tensor.Scalar;

public class LogSplit implements OfflineLogListener {
  private final NavigableMap<Integer, Integer> navigableMap = new TreeMap<>();
  private final LogSplitPredicate logSplitPredicate;
  private int index = 0;
  private Integer lo = null;

  public LogSplit(LogSplitPredicate logSplitPredicate) {
    this.logSplitPredicate = logSplitPredicate;
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    boolean crossing = logSplitPredicate.split(time, channel, byteBuffer);
    if (crossing) {
      if (Objects.nonNull(lo))
        navigableMap.put(lo, index - 1);
      lo = index;
    }
    ++index;
  }

  public NavigableMap<Integer, Integer> navigableMap() {
    if (Objects.nonNull(lo))
      navigableMap.put(lo, index - 1);
    return Collections.unmodifiableNavigableMap(navigableMap);
  }
}
