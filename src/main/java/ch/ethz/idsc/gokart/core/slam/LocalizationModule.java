// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LocalizationModule extends AbstractModule {
  private final Vlp16LcmHandler vlp16LcmHandler = new Vlp16LcmHandler(GokartLcmChannel.VLP16_CENTER);
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final LidarGyroLocalization lidarGyroLocalization = DubendorfLocalization.INSTANCE.lidarGyroLocalization;

  @Override
  protected void first() throws Exception {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(lidarGyroLocalization);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
    // ---
    davisImuLcmClient.addListener(lidarGyroLocalization);
    // ---
    vlp16LcmHandler.startSubscriptions();
    davisImuLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    vlp16LcmHandler.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
  }
}
