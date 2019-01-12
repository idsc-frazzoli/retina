// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.retina.lidar.LidarRotationEvent;
import ch.ethz.idsc.retina.lidar.LidarRotationListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.mark8.Mark8SpacialProvider;

enum Mark8DecoderExec {
  ;
  public static void main(String[] args) throws Exception {
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(new LidarRotationListener() {
      @Override
      public void lidarRotation(LidarRotationEvent lidarRotationEvent) {
        System.out.println("rotation " + lidarRotationEvent.usec + " " + lidarRotationEvent.rotation);
        // System.out.println(lidarRotationEvent);
      }
    });
    Mark8LcmClient mark8LcmClient = new Mark8LcmClient("center");
    mark8LcmClient.mark8Decoder.addRayListener(lidarRotationProvider);
    mark8LcmClient.mark8Decoder.addRayListener(new Mark8SpacialProvider());
    mark8LcmClient.startSubscriptions();
    Thread.sleep(10000);
  }
}
