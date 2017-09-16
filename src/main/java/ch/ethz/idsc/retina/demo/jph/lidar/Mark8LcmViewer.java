// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneRayFrame;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Inspector;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8SpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmClient;

enum Mark8LcmViewer {
  ;
  public static void main(String[] args) {
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.create3d(30000 * 8);
    Mark8SpacialProvider mark8SpacialProvider = new Mark8SpacialProvider();
    mark8SpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    Mark8Decoder mark8Decoder = new Mark8Decoder();
    mark8Decoder.addRayListener(mark8SpacialProvider);
    mark8Decoder.addRayListener(lidarRotationProvider);
    mark8Decoder.addRayListener(new Mark8Inspector());
    // ---
    VelodyneRayFrame velodyneFiringFrame = new VelodyneRayFrame();
    lidarAngularFiringCollector.addListener(velodyneFiringFrame);
    // ---
    Mark8LcmClient mark8LcmClient = new Mark8LcmClient(mark8Decoder, "center");
    mark8LcmClient.startSubscriptions();
  }
}
