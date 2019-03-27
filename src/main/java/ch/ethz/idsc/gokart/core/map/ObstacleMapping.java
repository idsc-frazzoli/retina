//code by ynager, gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;

/** class interprets sensor data from lidar */
public class ObstacleMapping extends AbstractBayesianMapping {
  public ObstacleMapping() {
    super(MappingConfig.GLOBAL.createBayesianOccupancyGrid(), //
            SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), -1, 1000);
  }
}
