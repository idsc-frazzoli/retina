// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;

/** j2b2 project
 * http://hakenberg.de/automation/j2b2_competition.htm */
public class Se2MultiresGrids {
  /** .
   * 20190331 ante decr == 0.6
   * 20190331 post decr == 0.55 */
  private static final Scalar DECR = DoubleScalar.of(0.55);
  // ---
  private final List<Se2Grid> list = new ArrayList<>();

  /** @param shift may be in physical space or pixel space
   * @param angle in radians
   * @param fan
   * @param levels */
  public Se2MultiresGrids(Scalar shift, Scalar angle, final int fan, int levels) {
    for (int iterate = 0; iterate < levels; ++iterate) {
      list.add(new Se2Grid(shift, angle, fan));
      // ---
      angle = angle.multiply(DECR);
      shift = shift.multiply(DECR);
    }
  }

  public Se2Grid grid(int level) {
    return list.get(level);
  }

  public int levels() {
    return list.size();
  }
}
