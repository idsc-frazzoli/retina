// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmServers;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

public class Vlp16RayLcmServerModule extends VelodyneLcmServerModule {
  public Vlp16RayLcmServerModule() {
    super(VelodyneLcmServers.ray(VelodyneModel.VLP16, "center", VelodyneStatics.RAY_DEFAULT_PORT));
  }
}
