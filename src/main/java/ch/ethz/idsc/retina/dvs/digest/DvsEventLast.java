// code by jph
package ch.ethz.idsc.retina.dvs.digest;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

public class DvsEventLast implements DvsEventDigest {
  // TODO LT use integer as index -> DvsEvent[]
  private final Map<Point, DvsEvent> map = new HashMap<>();

  @Override
  public void digest(DvsEvent dvsEvent) {
    map.put(dvsEvent.point(), dvsEvent);
  }

  public DvsEvent get(int x, int y) {
    return map.get(new Point(x, y));
  }

  public boolean contains(int x, int y) {
    return map.containsKey(new Point(x, y));
  }

  public Collection<Point> keys() {
    return map.keySet();
  }
}
