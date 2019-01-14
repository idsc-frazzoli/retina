// code by mg
package ch.ethz.idsc.demo.mg.filter;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** Implementation of background activity filter as presented in
 * "Frame-free dynamic digial vision" by Tobi Delbruck
 * Events on the image boarders are always filtered.
 * Smaller threshold_us results in more aggressive filtering */
public class BackgroundActivityFilter extends AbstractFilterHandler {
  private final int x_last;
  private final int y_last;
  /** timestamps initialized to 0 */
  private final int[][] timeStamps;
  private final int threshold_us;

  public BackgroundActivityFilter(int width, int height, int threshold_us) {
    x_last = width - 1;
    y_last = height - 1;
    timeStamps = new int[width][height];
    this.threshold_us = threshold_us;
  }

  @Override // from DavisDvsEventFilter
  public boolean filter(DavisDvsEvent davisDvsEvent) {
    // TODO MG investigate why siliconEye data sometimes wrong
    if (davisDvsEvent.x <= x_last && davisDvsEvent.y <= y_last) {
      updateNeighboursTimeStamps(davisDvsEvent.x, davisDvsEvent.y, davisDvsEvent.time);
      return davisDvsEvent.time - timeStamps[davisDvsEvent.x][davisDvsEvent.y] <= threshold_us;
    }
    return false;
  }

  /** updates all neighboring cells with the time stamp of the incoming event
   * 
   * @param x left-right pixel location, x=0 corresponds to far left
   * @param y up-down pixel location, y=0 corresponds to far up
   * @param time [us] */
  private void updateNeighboursTimeStamps(int x, int y, int time) {
    // check if we are not on an edge and then update all 8 neighbors
    if (x != 0 && x != x_last && y != 0 && y != y_last) {
      timeStamps[x - 1][y - 1] = time;
      timeStamps[x - 1][y] = time;
      timeStamps[x - 1][y + 1] = time;
      timeStamps[x][y - 1] = time;
      timeStamps[x][y + 1] = time;
      timeStamps[x + 1][y - 1] = time;
      timeStamps[x + 1][y] = time;
      timeStamps[x + 1][y + 1] = time;
    }
  }
}
