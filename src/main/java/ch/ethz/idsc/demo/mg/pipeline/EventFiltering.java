// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides event filters
public class EventFiltering {
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private final int[][] timestamps = new int[WIDTH][HEIGHT];

  // update all neighboring cells with the timestamp of the incoming event
  void updateNeighboursTimestamps(int x, int y, int time) {
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
  boolean backgroundActivityFilter(DavisDvsEvent davisDvsEvent, double filterConstant) {
    updateNeighboursTimestamps(davisDvsEvent.x, davisDvsEvent.y, davisDvsEvent.time);
    return davisDvsEvent.time - timestamps[davisDvsEvent.x][davisDvsEvent.y] <= filterConstant;
  }
}
