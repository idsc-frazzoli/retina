// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class Se2Grid {
  private final List<Se2GridPoint> list;

  /** size and complexity grows cubic in fan
   * 
   * @param shift
   * @param angle
   * @param fan typically 1, or 2 */
  public Se2Grid(Scalar shift, Scalar angle, final int fan) {
    List<Se2GridPoint> list = new ArrayList<>();
    for (int x = -fan; x <= fan; ++x)
      for (int y = -fan; y <= fan; ++y)
        for (int t = -fan; t <= fan; ++t)
          list.add(new Se2GridPoint(Tensors.vector(x, y, t), shift, angle));
    this.list = Collections.unmodifiableList(list);
  }

  /** @return unmodifiable list */
  public List<Se2GridPoint> gridPoints() {
    return list;
  }
}
