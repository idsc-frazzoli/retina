// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.geom.Point2D;

public interface SlamScore {
  /** @param point2D
   * @return integer in the range [0, 1, ..., 255] */
  int evaluate(Point2D point2D);
}
