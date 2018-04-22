// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;

/* package */ class Se2Grid {
  private final List<Se2GridPoint> list = new ArrayList<>();

  public Se2Grid(Scalar shift, Scalar angle, final int fan) {
    for (int x = -fan; x <= fan; ++x)
      for (int y = -fan; y <= fan; ++y)
        for (int t = -fan; t <= fan; ++t)
          list.add(new Se2GridPoint(shift, angle, x, y, t));
  }

  public List<Se2GridPoint> gridPoints() {
    return list;
  }
}
