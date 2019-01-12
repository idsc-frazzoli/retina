// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.gokart.lcm.LcmClientInterface;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxSpacialProvider;

public class Urg04lxLcmHandler implements LcmClientInterface {
  private final Urg04lxLcmClient urg04lxLcmClient;
  public final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(Urg04lxDevice.MAX_POINTS, 2);

  public Urg04lxLcmHandler(String lidarId) {
    urg04lxLcmClient = new Urg04lxLcmClient(lidarId);
    // THE ORDER IS IMPORTANT:
    // 1) update spacial info
    // 2) create rotation event
    Urg04lxSpacialProvider urg04lxSpacialProvider = new Urg04lxSpacialProvider(2);
    urg04lxLcmClient.urg04lxDecoder.addRayListener(urg04lxSpacialProvider);
    // ---
    urg04lxSpacialProvider.addListener(lidarAngularFiringCollector);
    // ---
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    urg04lxLcmClient.urg04lxDecoder.addRayListener(lidarRotationProvider);
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
  }

  public Urg04lxDecoder urg04lxDecoder() {
    return urg04lxLcmClient.urg04lxDecoder;
  }

  @Override
  public void startSubscriptions() {
    urg04lxLcmClient.startSubscriptions();
  }

  @Override
  public void stopSubscriptions() {
    urg04lxLcmClient.stopSubscriptions();
  }
}
