// code by mg
package ch.ethz.idsc.demo.mg.filter;

import ch.ethz.idsc.demo.mg.slam.config.DvsConfig;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Scalars;

/** based on paper "Fast event-based corner detection"
 * C++ code is available under https://github.com/uzh-rpg/rpg_corner_events
 * http://rpg.ifi.uzh.ch/docs/BMVC17_Mueggler.pdf
 * Event is always filtered if closer than {@link margin} to the boarder */
public class CornerDetector extends AbstractFilterHandler {
  /** hard coded circle parameters for corner detector */
  private static final int[][] CIRCLE3 = { //
      { 0, 3 }, { 1, 3 }, { 2, 2 }, { 3, 1 }, //
      { 3, 0 }, { 3, -1 }, { 2, -2 }, { 1, -3 }, //
      { 0, -3 }, { -1, -3 }, { -2, -2 }, { -3, -1 }, //
      { -3, 0 }, { -3, 1 }, { -2, 2 }, { -1, 3 } };
  private static final int[][] CIRCLE4 = { //
      { 0, 4 }, { 1, 4 }, { 2, 3 }, { 3, 2 }, { 4, 1 }, //
      { 4, 0 }, { 4, -1 }, { 3, -2 }, { 2, -3 }, { 1, -4 }, //
      { 0, -4 }, { -1, -4 }, { -2, -3 }, { -3, -2 }, { -4, -1 }, //
      { -4, 0 }, { -4, 1 }, { -3, 2 }, { -2, 3 }, { -1, 4 } };
  // ---
  private final int width;
  private final int height;
  private final int margin;
  /** surface of active events for both polarities */
  private final int[][][] SAE;

  public CornerDetector(DvsConfig davisConfig) {
    width = Scalars.intValueExact(davisConfig.width);
    height = Scalars.intValueExact(davisConfig.height);
    margin = Scalars.intValueExact(davisConfig.margin);
    SAE = new int[width][height][2];
  }

  @Override // from DavisDvsEventFilter
  public boolean filter(DavisDvsEvent e) {
    // update SAE
    int pol = e.i;
    SAE[e.x][e.y][pol] = e.time;
    // check if not too close to boarder
    if (e.x < margin || e.x > width - margin - 1 || e.y < margin || e.y > height - margin - 1)
      return false;
    if (findStreak(CIRCLE3, 3, 6, e, pol))
      return findStreak(CIRCLE4, 4, 8, e, pol);
    return false;
  }

  private boolean findStreak(int[][] circle, int streakSizeMin, int streakSizeMax, DavisDvsEvent e, int pol) {
    boolean found_streak = false;
    for (int i = 0; i < circle.length; ++i) {
      for (int streak_size = streakSizeMin; streak_size <= streakSizeMax; streak_size++) {
        // check that first event is larger than neighbor
        if (SAE[e.x + circle[i][0]][e.y + circle[i][1]][pol] < SAE[e.x + circle[(i - 1 + circle.length) % circle.length][0]][e.y
            + circle[(i - 1 + circle.length) % circle.length][1]][pol])
          continue;
        // check that streak event is larger than neighbor
        if (SAE[e.x + circle[(i + streak_size - 1) % circle.length][0]][e.y
            + circle[(i + streak_size - 1) % circle.length][1]][pol] < SAE[e.x + circle[(i + streak_size) % circle.length][0]][e.y
                + circle[(i + streak_size) % circle.length][1]][pol])
          continue;
        //
        double min_t = SAE[e.x + circle[i][0]][e.y + circle[i][1]][pol];
        for (int j = 1; j < streak_size; j++) {
          double tj = SAE[e.x + circle[(i + j) % circle.length][0]][e.y + circle[(i + j) % circle.length][1]][pol];
          if (tj < min_t)
            min_t = tj;
        }
        //
        boolean did_break = false;
        for (int j = streak_size; j < circle.length; j++) {
          double tj = SAE[e.x + circle[(i + j) % circle.length][0]][e.y + circle[(i + j) % circle.length][1]][pol];
          if (tj >= min_t) {
            did_break = true;
            break;
          }
        }
        //
        if (!did_break) {
          found_streak = true;
          break;
        }
      }
      if (found_streak) {
        return true;
      }
    }
    return false;
  }
}
