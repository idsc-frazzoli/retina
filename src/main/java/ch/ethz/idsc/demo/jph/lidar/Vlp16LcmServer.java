// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmServer;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

enum Vlp16LcmServer {
  ;
  public static void main(String[] args) {
    VelodyneLcmServer velodyneLcmServer = new VelodyneLcmServer(VelodyneModel.VLP16, "center", //
        VelodyneStatics.RAY_DEFAULT_PORT, VelodyneStatics.POS_DEFAULT_PORT);
    velodyneLcmServer.start();
  }
}
