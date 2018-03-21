//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** Provides a Surface of Active Events data structure. */
public class DavisSurfaceOfActiveEvents {
  // Actually I want to import the two values below from Davis240c.java
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private int[][] timestamps = new int[WIDTH][HEIGHT];

  void updateSurface(DavisDvsEvent davisDvsEvent) {
    this.timestamps[davisDvsEvent.x][davisDvsEvent.y] = davisDvsEvent.time;
  }

  // update all neighboring cells with the timestamp of the incoming event
  void updateNeighboursTimestamps(DavisDvsEvent davisDvsEvent) {
    int x = davisDvsEvent.x;
    int y = davisDvsEvent.y;
    int newEventTime = davisDvsEvent.time;
    // check if we are not on the edge and then update all 8 neighbours
    if (x != 0 && x != (WIDTH - 1) && y != 0 && y != (HEIGHT - 1)) {
      this.timestamps[x - 1][y] = newEventTime;
      this.timestamps[x + 1][y] = newEventTime;
      this.timestamps[x - 1][y - 1] = newEventTime;
      this.timestamps[x][y - 1] = newEventTime;
      this.timestamps[x + 1][y - 1] = newEventTime;
      this.timestamps[x - 1][y + 1] = newEventTime;
      this.timestamps[x][y + 1] = newEventTime;
      this.timestamps[x + 1][y + 1] = newEventTime;
    }
    // TODO: update neighbours for the four corner lines (or maybe just ignore?)
  }

  // this function implements a very simple noise filter
  Boolean backgroundActivityFilter(DavisDvsEvent davisDvsEvent, double filterConstant) {
    updateNeighboursTimestamps(davisDvsEvent);
    int surfaceEntry = this.timestamps[davisDvsEvent.x][davisDvsEvent.y];
    int newEventTime = davisDvsEvent.time;
    if (surfaceEntry + filterConstant < newEventTime) {
      return false;
    } else {
      return true;
    }
  }
}
