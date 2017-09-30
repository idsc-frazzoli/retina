// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.app.VelodyneRayFrame;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmHandler;

enum Mark8LcmViewer {
  ;
  public static void main(String[] args) {
    Mark8LcmHandler mark8LcmHandler = new Mark8LcmHandler("center");
    // ---
    VelodyneRayFrame velodyneFiringFrame = new VelodyneRayFrame();
    mark8LcmHandler.lidarAngularFiringCollector.addListener(velodyneFiringFrame);
  }
}
