// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import java.util.Objects;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmServer;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

abstract class VelodyneLcmServerModule extends AbstractModule {
  private VelodyneLcmServer velodyneLcmServer = null;
  private final VelodyneModel velodyneModel;
  private final String channel;

  public VelodyneLcmServerModule(VelodyneModel velodyneModel, String channel) {
    this.velodyneModel = velodyneModel;
    this.channel = channel;
  }

  @Override
  protected void first() throws Exception {
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
