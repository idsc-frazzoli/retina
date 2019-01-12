// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.mark8.Mark8SpacialProvider;

public class Mark8LcmHandler {
  /** in the workshop, the lidar produces 37000 points per revolution
   * the value here is a crude upper bound */
  public static final int MAX_COORDINATES = 10500 * 8;
  // ---
  private final Mark8LcmClient mark8LcmClient;
  public final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(MAX_COORDINATES, 3);

  public Mark8LcmHandler(String lidarId) {
    mark8LcmClient = new Mark8LcmClient(lidarId);
    // ---
    Mark8SpacialProvider mark8SpacialProvider = new Mark8SpacialProvider();
    mark8SpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    // ---
    mark8LcmClient.mark8Decoder.addRayListener(mark8SpacialProvider);
    mark8LcmClient.mark8Decoder.addRayListener(lidarRotationProvider);
    // ---
    mark8LcmClient.startSubscriptions();
  }
}
