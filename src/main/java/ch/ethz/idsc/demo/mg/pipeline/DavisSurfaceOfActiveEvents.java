//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** Provides a Surface of Active Events data structure. */
public class DavisSurfaceOfActiveEvents {
  private static final int WIDTH = 240; // maybe import these two values from some other file?
  private static final int HEIGHT = 180;
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
  Boolean backgroundActivityFilter(DavisDvsEvent davisDvsEvent, double filterConstant) {
    updateNeighboursTimestamps(davisDvsEvent);
    int surfaceEntry = timestamps[davisDvsEvent.x][davisDvsEvent.y];
    int newEventTime = davisDvsEvent.time;
    // TODO problematic time calculation: only integer differences between two time stamps "make sense"
    // in this case the computation should be: newEventTime - surfaceEntry <= filterConstant
    return newEventTime <= surfaceEntry + filterConstant; // FIXME
    // if (surfaceEntry + filterConstant < newEventTime) {
    // return false;
    // } else {
    // return true;
    // }
  }
}
