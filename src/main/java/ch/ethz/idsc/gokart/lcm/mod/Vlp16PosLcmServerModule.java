// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmServers;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.sys.StartAndStoppableModule;

public class Vlp16PosLcmServerModule extends StartAndStoppableModule {
  public Vlp16PosLcmServerModule() {
    super(VelodyneLcmServers.pos(VelodyneModel.VLP16, "center", VelodyneStatics.POS_DEFAULT_PORT));
  }
}
