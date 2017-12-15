// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.io.IOException;

import ch.ethz.idsc.retina.alg.slam.OccupancyMap;
import ch.ethz.idsc.retina.alg.slam.Se2MultiresSamples;
import ch.ethz.idsc.retina.alg.slam.SlamFrame;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.tensor.RealScalar;

public enum Vlp16PlanarSlam {
  ;
  public static void main(String[] args) throws InterruptedException, IOException {
    float METER_TO_PIXEL = 8f;
    Se2MultiresSamples se2MultiresSamples = new Se2MultiresSamples( //
        RealScalar.of(0.03 * METER_TO_PIXEL), // 3 [cm]
        RealScalar.of(5 * Math.PI / 180), // 2 [deg]
        4);
    OccupancyMap occupancyMap = new OccupancyMap(METER_TO_PIXEL, se2MultiresSamples);
    // ---
    final String lidarId = GokartLcmChannel.VLP16_CENTER;
    VelodyneModel velodyneModel = VelodyneModel.VLP16;
    VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    // ---
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.vlp16_p01deg();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(occupancyMap);
    // ---
    SlamFrame slamFrame = new SlamFrame(occupancyMap);
    occupancyMap.addListener(slamFrame.slamComponent);
    velodyneLcmClient.startSubscriptions();
  }
}
