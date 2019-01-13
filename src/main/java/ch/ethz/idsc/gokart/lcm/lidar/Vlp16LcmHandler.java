// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.gokart.lcm.LcmClientInterface;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SpacialProvider;

public class Vlp16LcmHandler implements LcmClientInterface {
  public static final int MAX_COORDINATES = 2304 * 32;
  // ---
  public final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  // FIXME this is coupled!!
  public final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(MAX_COORDINATES, 3);
  private final VelodyneLcmClient velodyneLcmClient;
  public final Vlp16SpacialProvider lidarSpacialProvider;

  public Vlp16LcmHandler(String lidarId, double angle_offset) {
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    lidarSpacialProvider = new Vlp16SpacialProvider(angle_offset);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  @Override // from LcmClientInterface
  public void startSubscriptions() {
    velodyneLcmClient.startSubscriptions();
  }

  @Override // from LcmClientInterface
  public void stopSubscriptions() {
    velodyneLcmClient.stopSubscriptions();
  }
}
