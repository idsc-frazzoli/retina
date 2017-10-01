// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.io.IOException;

import ch.ethz.idsc.retina.alg.slam.OccupancyMap;
import ch.ethz.idsc.retina.alg.slam.SlamFrame;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePlanarEmulator;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

public enum Hdl32ePlanarSlam {
  ;
  public static void main(String[] args) throws InterruptedException, IOException {
    OccupancyMap occupancyMap = new OccupancyMap();
    // ---
    final String lidarId = "center";
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    VelodyneDecoder velodyneDecoder = new Hdl32eDecoder();
    VelodyneLcmClient velodyneLcmClient = new VelodyneLcmClient(velodyneModel, velodyneDecoder, lidarId);
    // ---
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = new Hdl32ePlanarEmulator();
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
