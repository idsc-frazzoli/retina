// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxRangeProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.app.Urg04lxFrame;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmClient;

enum Urg04lxViewerLcmClient {
  ;
  public static void main(String[] args) {
    Urg04lxDecoder urg04lxDecoder = new Urg04lxDecoder();
    Urg04lxLcmClient urg04lxLcmClient = new Urg04lxLcmClient(urg04lxDecoder, "front");
    Urg04lxFrame urg04lxFrame = new Urg04lxFrame();
    {
      Urg04lxRangeProvider urg04lxRangeProvider = new Urg04lxRangeProvider();
      urg04lxRangeProvider.addListener(urg04lxFrame);
      urg04lxDecoder.addRayListener(urg04lxRangeProvider);
    }
    {
      LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
      urg04lxDecoder.addRayListener(lidarRotationProvider);
      Urg04lxSpacialProvider urg04lxSpacialProvider = new Urg04lxSpacialProvider(2);
      LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(Urg04lxDevice.MAX_POINTS, 2);
      lidarRotationProvider.addListener(lidarAngularFiringCollector);
      lidarAngularFiringCollector.addListener(urg04lxFrame);
      urg04lxSpacialProvider.addListener(lidarAngularFiringCollector);
      urg04lxDecoder.addRayListener(urg04lxSpacialProvider);
    }
    urg04lxLcmClient.startSubscriptions();
  }
}
