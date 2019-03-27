// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

/** class interprets sensor data from lidar */
public class TrackMapping extends AbstractBayesianMapping {
  public TrackMapping() {
    super(MappingConfig.GLOBAL.createTrackFittingBayesianOccupancyGrid(), //
        TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate(), -6, 200);
  }
}
