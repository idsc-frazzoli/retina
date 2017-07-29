// code by jph
package ch.ethz.idsc.retina.dvs.digest;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.util.data.IntDisjointSet;

public class DvsEventComponents implements DvsEventDigest {
  private final Dimension dimension;
  private final IntDisjointSet disjointSet;
  private final DvsEventLast dvsEventLast;

  public DvsEventComponents(Dimension dimension, DvsEventLast dvsEventLast) {
    this.dimension = dimension;
    disjointSet = new IntDisjointSet(dimension.width * dimension.height);
    this.dvsEventLast = dvsEventLast;
  }

  @Override
  public void digest(DvsEvent dvsEvent) {
    int index = indexOf(dvsEvent.x, dvsEvent.y);
    // left right, up down
    consider(index, dvsEvent.x - 1, dvsEvent.y + 0);
    consider(index, dvsEvent.x + 1, dvsEvent.y + 0);
    consider(index, dvsEvent.x + 0, dvsEvent.y - 1);
    consider(index, dvsEvent.x + 0, dvsEvent.y + 1);
    // diagonals
    consider(index, dvsEvent.x - 1, dvsEvent.y - 1);
    consider(index, dvsEvent.x - 1, dvsEvent.y + 1);
    consider(index, dvsEvent.x + 1, dvsEvent.y + 1);
    consider(index, dvsEvent.x + 1, dvsEvent.y - 1);
  }

  private void consider(int index, int x, int y) {
    if (0 <= x && //
        0 <= y && //
        x < dimension.width && //
        y < dimension.height && //
        dvsEventLast.contains(x, y))
      disjointSet.union(index, indexOf(x, y));
  }

  public int at(int x, int y) {
    if (dvsEventLast.contains(x, y))
      return disjointSet.find(indexOf(x, y));
    return -1;
  }

  public Collection<Integer> reps() {
    Collection<Integer> list = new HashSet<>();
    for (Point point : dvsEventLast.keys()) {
      int index = indexOf(point.x, point.y);
      list.add(disjointSet.find(index));
    }
    return list;
  }

  private int indexOf(int x, int y) {
    return x + dimension.height * y;
  }
}
