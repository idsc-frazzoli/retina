// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class BackgroundActivityFilter implements FilteringPipeline {
  private final int width;
  private final int height;
  private final int[][] timestamps;
  private final double filterConstant;
  // ---
  private double eventCount;
  private double filteredEventCount;

  public BackgroundActivityFilter(DavisConfig davisConfig) {
    width = davisConfig.width.number().intValue();
    height = davisConfig.height.number().intValue();
    timestamps = new int[width][height];
    filterConstant = davisConfig.filterConstant.number().doubleValue();
  }

  // events on the image boarders are always filtered. smaller filterConstant results in more aggressive filtering.
  @Override
  public boolean filterPipeline(DavisDvsEvent davisDvsEvent) {
    ++eventCount;
    updateNeighboursTimestamps(davisDvsEvent.x, davisDvsEvent.y, davisDvsEvent.time);
    if (davisDvsEvent.time - timestamps[davisDvsEvent.x][davisDvsEvent.y] <= filterConstant) {
      return true;
    }
    filteredEventCount++;
    return false;
  }

  /** update all neighboring cells with the time stamp of the incoming event
   * 
   * @param x
   * @param y
   * @param time */
  private void updateNeighboursTimestamps(int x, int y, int time) {
    // check if we are not on an edge and then update all 8 neighbors
    if (x != 0 && x != width - 1 && y != 0 && y != height - 1) {
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

  /** returns the percentage of events that are filtered out */
  @Override
  public double getFilteredPercentage() {
    return 100 * filteredEventCount / eventCount;
  }
}
