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

  // this function implements a very simple noise filter
  Boolean backgroundActivityFilter(DavisDvsEvent davisDvsEvent, double filterConstant) {
    int surfaceEntry = this.timestamps[davisDvsEvent.x][davisDvsEvent.y];
    int newEventTime = davisDvsEvent.time;
    if (surfaceEntry + filterConstant < newEventTime) {
      return false;
    } else {
      return true;
    }
  }
}
