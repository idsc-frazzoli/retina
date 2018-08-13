// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** provides event filters */
// TODO MG should be split into 2 classes: BackgroundActivityFilter and CornerDetector
// ... that implement the interface with function "boolean filterPipeline(DavisDvsEvent davisDvsEvent)"
public class EventFiltering {
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
  private final int filterConfig;
  private double eventCount;
  private double filteredEventCount;
  // for background activity filter
  private final BackgroundActivityFilter backgroundActivityFilter;
  // for corner detector
  private final int margin; // events too close to image border are neglected
  /** surface of active events for each polarity */
  private final int[][][] SAE;

  public EventFiltering(DavisConfig davisConfig) {
    width = davisConfig.width.number().intValue();
    height = davisConfig.height.number().intValue();
    filterConfig = davisConfig.filterConfig.number().intValue();
    backgroundActivityFilter = new BackgroundActivityFilter(davisConfig);
    margin = davisConfig.margin.number().intValue();
    SAE = new int[width][height][2];
  }

  // possibility to apply various filters, e.g. filter specific region of interest plus backgroundActivity filter
  public boolean filterPipeline(DavisDvsEvent davisDvsEvent) {
    ++eventCount;
    if (filterConfig == 0 && backgroundActivityFilter.filterPipeline(davisDvsEvent)) {
      ++filteredEventCount;
      return true;
    }
    if (filterConfig == 1 && cornerDetector(davisDvsEvent)) {
      ++filteredEventCount;
      return true;
    }
    return false;
  }

  /** based on paper "Fast event-based corner detection".
   * C++ code is available under https://github.com/uzh-rpg/rpg_corner_events
   * http://rpg.ifi.uzh.ch/docs/BMVC17_Mueggler.pdf
   * 
   * @param e
   * @return */
  private boolean cornerDetector(DavisDvsEvent e) {
    // update SAE
    int pol = e.i;
    SAE[e.x][e.y][pol] = e.time;
    // check if not too close to boarder
    if (e.x < margin || e.x > width - margin - 1 || e.y < margin || e.y > height - margin - 1) {
      return false;
    }
    boolean found_streak = false;
    // TODO MG circle3 and circle4 logics are redundant -> single function and reuse?
    for (int i = 0; i < CIRCLE3.length; ++i) {
      for (int streak_size = 3; streak_size <= 6; streak_size++) {
        // check that first event is larger than neighbor
        if (SAE[e.x + CIRCLE3[i][0]][e.y + CIRCLE3[i][1]][pol] < SAE[e.x + CIRCLE3[(i - 1 + 16) % 16][0]][e.y + CIRCLE3[(i - 1 + 16) % 16][1]][pol])
          continue;
        // check that streak event is larger than neighbor
        if (SAE[e.x + CIRCLE3[(i + streak_size - 1) % 16][0]][e.y
            + CIRCLE3[(i + streak_size - 1) % 16][1]][pol] < SAE[e.x + CIRCLE3[(i + streak_size) % 16][0]][e.y + CIRCLE3[(i + streak_size) % 16][1]][pol])
          continue;
        //
        double min_t = SAE[e.x + CIRCLE3[i][0]][e.y + CIRCLE3[i][1]][pol];
        for (int j = 1; j < streak_size; j++) {
          double tj = SAE[e.x + CIRCLE3[(i + j) % 16][0]][e.y + CIRCLE3[(i + j) % 16][1]][pol];
          if (tj < min_t)
            min_t = tj;
        }
        //
        boolean did_break = false;
        for (int j = streak_size; j < CIRCLE3.length; j++) {
          double tj = SAE[e.x + CIRCLE3[(i + j) % 16][0]][e.y + CIRCLE3[(i + j) % 16][1]][pol];
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
        break;
      }
    }
    if (found_streak) {
      found_streak = false;
      for (int i = 0; i < CIRCLE4.length; ++i) {
        for (int streak_size = 4; streak_size <= 8; streak_size++) {
          if (SAE[e.x + CIRCLE4[i][0]][e.y + CIRCLE4[i][1]][pol] < SAE[e.x + CIRCLE4[(i - 1 + CIRCLE4.length) % CIRCLE4.length][0]][e.y
              + CIRCLE4[(i - 1 + 20) % 20][1]][pol])
            continue;
          if (SAE[e.x + CIRCLE4[(i + streak_size - 1) % CIRCLE4.length][0]][e.y
              + CIRCLE4[(i + streak_size - 1) % CIRCLE4.length][1]][pol] < SAE[e.x + CIRCLE4[(i + streak_size) % CIRCLE4.length][0]][e.y
                  + CIRCLE4[(i + streak_size) % 20][1]][pol])
            continue;
          //
          double min_t = SAE[e.x + CIRCLE4[i][0]][e.y + CIRCLE4[i][1]][pol];
          for (int j = 1; j < streak_size; j++) {
            double tj = SAE[e.x + CIRCLE4[(i + j) % CIRCLE4.length][0]][e.y + CIRCLE4[(i + j) % 20][1]][pol];
            if (tj < min_t)
              min_t = tj;
          }
          //
          boolean did_break = false;
          for (int j = streak_size; j < CIRCLE4.length; j++) {
            double tj = SAE[e.x + CIRCLE4[(i + j) % CIRCLE4.length][0]][e.y + CIRCLE4[(i + j) % 20][1]][pol];
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
          break;
        }
      }
    }
    return found_streak;
  }

  public double getEventCount() {
    return eventCount;
  }

  public double getFilteredPercentage() {
    return 100 * filteredEventCount / eventCount;
  }
}
