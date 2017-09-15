// code by jph
package ch.ethz.idsc.retina.lcm.mod;

import java.util.Objects;

import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmServer;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class Hdl32eLcmServerModule extends AbstractModule {
  private VelodyneLcmServer velodyneLcmServer = null;

  @Override
  protected void first() throws Exception {
    VelodyneModel velodyneModel = VelodyneModel.HDL32E;
    String channel = "center";
    int portRay = VelodyneStatics.RAY_DEFAULT_PORT;
    int portPos = VelodyneStatics.POS_DEFAULT_PORT;
    velodyneLcmServer = new VelodyneLcmServer(velodyneModel, channel, portRay, portPos);
    velodyneLcmServer.start();
  }

  @Override
  protected void last() {
    if (Objects.nonNull(velodyneLcmServer)) {
      velodyneLcmServer.stop();
      velodyneLcmServer = null;
    }
  }
}
