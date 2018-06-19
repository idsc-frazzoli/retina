// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmServer;

enum Vlp16LcmServer {
  ;
  public static void main(String[] args) {
    VelodyneLcmServer velodyneLcmServer = new VelodyneLcmServer(VelodyneModel.VLP16, "center", //
        VelodyneStatics.RAY_DEFAULT_PORT, VelodyneStatics.POS_DEFAULT_PORT);
    velodyneLcmServer.start();
  }
}
