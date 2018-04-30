// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides event filters
public class EventFiltering {
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  // for background activity filter
  private final int[][] timestamps = new int[WIDTH][HEIGHT];
  // for corner detector
  private int boarder = 4; // events too close to image boarder are neglected
  private int[][][] SAE = new int[WIDTH][HEIGHT][2]; // surface of active events for each polarity
  // hard coded circle parameters for corner detector
  private final int[][] circle3 = { { 0, 3 }, { 1, 3 }, { 2, 2 }, { 3, 1 }, { 3, 0 }, { 3, -1 }, { 2, -2 }, { 1, -3 }, { 0, -3 }, { -1, -3 }, { -2, -2 },
      { -3, -1 }, { -3, 0 }, { -3, 1 }, { -2, 2 }, { -1, 3 } };
  private final int[][] circle4 = { { 0, 4 }, { 1, 4 }, { 2, 3 }, { 3, 2 }, { 4, 1 }, { 4, 0 }, { 4, -1 }, { 3, -2 }, { 2, -3 }, { 1, -4 }, { 0, -4 },
      { -1, -4 }, { -2, -3 }, { -3, -2 }, { -4, -1 }, { -4, 0 }, { -4, 1 }, { -3, 2 }, { -2, 3 }, { -1, 4 } };

  public EventFiltering() {
    // ...
  }

  // possibility to apply various filters, e.g. filter specific region of interest plus backgroundActivity filter
  public boolean filterPipeline(DavisDvsEvent davisDvsEvent, double filterConstant) {
//    return backgroundActivityFilter(davisDvsEvent, filterConstant);
     return cornerDetector(davisDvsEvent);
  }

  // update all neighboring cells with the timestamp of the incoming event
  private void updateNeighboursTimestamps(int x, int y, int time) {
    // check if we are not on an edge and then update all 8 neighbours
    if (x != 0 && x != (WIDTH - 1) && y != 0 && y != (HEIGHT - 1)) {
      timestamps[x - 1][y] = time;
      timestamps[x + 1][y] = time;
      timestamps[x - 1][y - 1] = time;
      timestamps[x][y - 1] = time;
      timestamps[x + 1][y - 1] = time;
      timestamps[x - 1][y + 1] = time;
      timestamps[x][y + 1] = time;
      timestamps[x + 1][y + 1] = time;
    }
  }

  // events on the image boarders are always filtered. smaller filterConstant results in more aggressive filtering.
  private boolean backgroundActivityFilter(DavisDvsEvent davisDvsEvent, double filterConstant) {
    updateNeighboursTimestamps(davisDvsEvent.x, davisDvsEvent.y, davisDvsEvent.time);
    return davisDvsEvent.time - timestamps[davisDvsEvent.x][davisDvsEvent.y] <= filterConstant;
  }

  // based on paper "Fast event-based corner detection". C++ code is available under https://github.com/uzh-rpg/rpg_corner_events
  private boolean cornerDetector(DavisDvsEvent e) {
    // update SAE
    int pol = e.i;
    SAE[e.x][e.y][pol] = e.time;
    // check if not too close to boarder
    if (e.x < boarder || e.x > WIDTH - boarder - 1 || e.y < boarder || e.y > HEIGHT - boarder - 1) {
      return false;
    }
    boolean found_streak = false;
    for (int i = 0; i < 16; i++) {
      for (int streak_size = 3; streak_size <= 6; streak_size++) {
        // check that first event is larger than neighbor
        if (SAE[e.x + circle3[i][0]][e.y + circle3[i][1]][pol] < SAE[e.x + circle3[(i - 1 + 16) % 16][0]][e.y + circle3[(i - 1 + 16) % 16][1]][pol])
          continue;
        // check that streak event is larger than neighbor
        if (SAE[e.x + circle3[(i + streak_size - 1) % 16][0]][e.y
            + circle3[(i + streak_size - 1) % 16][1]][pol] < SAE[e.x + circle3[(i + streak_size) % 16][0]][e.y + circle3[(i + streak_size) % 16][1]][pol])
          continue;
        //
        double min_t = SAE[e.x + circle3[i][0]][e.y + circle3[i][1]][pol];
        for (int j = 1; j < streak_size; j++) {
          double tj = SAE[e.x + circle3[(i + j) % 16][0]][e.y + circle3[(i + j) % 16][1]][pol];
          if (tj < min_t)
            min_t = tj;
        }
        //
        boolean did_break = false;
        for (int j = streak_size; j < 16; j++) {
          double tj = SAE[e.x + circle3[(i + j) % 16][0]][e.y + circle3[(i + j) % 16][1]][pol];
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
      for (int i = 0; i < 20; i++) {
        for (int streak_size = 4; streak_size <= 8; streak_size++) {
          if (SAE[e.x + circle4[i][0]][e.y + circle4[i][1]][pol] < SAE[e.x + circle4[(i - 1 + 20) % 20][0]][e.y + circle4[(i - 1 + 20) % 20][1]][pol])
            continue;
          if (SAE[e.x + circle4[(i + streak_size - 1) % 20][0]][e.y
              + circle4[(i + streak_size - 1) % 20][1]][pol] < SAE[e.x + circle4[(i + streak_size) % 20][0]][e.y + circle4[(i + streak_size) % 20][1]][pol])
            continue;
          //
          double min_t = SAE[e.x + circle4[i][0]][e.y + circle4[i][1]][pol];
          for (int j = 1; j < streak_size; j++) {
            double tj = SAE[e.x + circle4[(i + j) % 20][0]][e.y + circle4[(i + j) % 20][1]][pol];
            if (tj < min_t)
              min_t = tj;
          }
          //
          boolean did_break = false;
          for (int j = streak_size; j < 20; j++) {
            double tj = SAE[e.x + circle4[(i + j) % 20][0]][e.y + circle4[(i + j) % 20][1]][pol];
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
}
