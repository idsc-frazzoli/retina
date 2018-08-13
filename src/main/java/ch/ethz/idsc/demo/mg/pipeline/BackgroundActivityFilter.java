// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

class BackgroundActivityFilter implements FilteringPipeline {
  private final int width;
  private final int height;
  private final int[][] timestamps;
  private final double filterConstant;

  public BackgroundActivityFilter(DavisConfig davisConfig) {
    width = davisConfig.width.number().intValue();
    height = davisConfig.height.number().intValue();
    // filterConfig = davisConfig.filterConfig.number().intValue();
    timestamps = new int[width][height];
    filterConstant = davisConfig.filterConstant.number().doubleValue();
  }

  // events on the image boarders are always filtered. smaller filterConstant results in more aggressive filtering.
  @Override
  public boolean filterPipeline(DavisDvsEvent davisDvsEvent) {
    updateNeighboursTimestamps(davisDvsEvent.x, davisDvsEvent.y, davisDvsEvent.time);
    return davisDvsEvent.time - timestamps[davisDvsEvent.x][davisDvsEvent.y] <= filterConstant;
  }

  /** update all neighboring cells with the timestamp of the incoming event
   * 
   * @param x
   * @param y
   * @param time */
  private void updateNeighboursTimestamps(int x, int y, int time) {
    // check if we are not on an edge and then update all 8 neighbours
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
}
