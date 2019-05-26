// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmServers;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

public class Vlp16PosLcmServerModule extends VelodyneLcmServerModule {
  public Vlp16PosLcmServerModule() {
    super(VelodyneLcmServers.pos(VelodyneModel.VLP16, "center", VelodyneStatics.POS_DEFAULT_PORT));
  }
}
