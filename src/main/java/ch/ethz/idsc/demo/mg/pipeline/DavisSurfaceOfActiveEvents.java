//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides a Surface of Active Events data structure.
public class DavisSurfaceOfActiveEvents {
  private static final int WIDTH = 240; // TODO import these two values from some other file?
  private static final int HEIGHT = 180;
  // fields
  private int[][] timestamps = new int[WIDTH][HEIGHT];

  void updateSurface(DavisDvsEvent davisDvsEvent) {
    timestamps[davisDvsEvent.x][davisDvsEvent.y] = davisDvsEvent.time;
  }

  // update all neighboring cells with the timestamp of the incoming event
  void updateNeighboursTimestamps(DavisDvsEvent davisDvsEvent) {
    int x = davisDvsEvent.x;
    int y = davisDvsEvent.y;
    int newEventTime = davisDvsEvent.time;
    // check if we are not on an edge and then update all 8 neighbours
    if (x != 0 && x != (WIDTH - 1) && y != 0 && y != (HEIGHT - 1)) {
      timestamps[x - 1][y] = newEventTime;
      timestamps[x + 1][y] = newEventTime;
      timestamps[x - 1][y - 1] = newEventTime;
      timestamps[x][y - 1] = newEventTime;
      timestamps[x + 1][y - 1] = newEventTime;
      timestamps[x - 1][y + 1] = newEventTime;
      timestamps[x][y + 1] = newEventTime;
      timestamps[x + 1][y + 1] = newEventTime;
    }
    // TODO: update neighbors for the four corner lines (or maybe just ignore?)
  }

  // this function implements a very simple noise filter
  boolean backgroundActivityFilter(DavisDvsEvent davisDvsEvent, double filterConstant) {
    updateNeighboursTimestamps(davisDvsEvent);
    return davisDvsEvent.time - timestamps[davisDvsEvent.x][davisDvsEvent.y] <= filterConstant;
  }
}
