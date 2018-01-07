// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import ch.ethz.idsc.retina.alg.slam.OccupancyMap;
import ch.ethz.idsc.retina.alg.slam.Se2MultiresSamples;
import ch.ethz.idsc.retina.alg.slam.SlamFrame;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;
import ch.ethz.idsc.tensor.RealScalar;

public enum Hdl32ePlanarSlam {
  ;
  public static void main(String[] args) {
    float METER_TO_PIXEL = 10f;
    Se2MultiresSamples se2MultiresSamples = new Se2MultiresSamples( //
        RealScalar.of(0.03 * METER_TO_PIXEL), // 3 [cm]
        RealScalar.of(2 * Math.PI / 180), // 2 [deg]
        4, 1);
    OccupancyMap occupancyMap = new OccupancyMap(METER_TO_PIXEL, se2MultiresSamples);
    // ---
    final String lidarId = "center";
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    VelodyneDecoder velodyneDecoder = new Hdl32eDecoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    // ---
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = VelodynePlanarEmulator.hdl32e();
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
