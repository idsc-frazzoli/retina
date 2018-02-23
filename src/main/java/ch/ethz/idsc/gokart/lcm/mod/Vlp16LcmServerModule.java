// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;

public class Vlp16LcmServerModule extends VelodyneLcmServerModule {
  public Vlp16LcmServerModule() {
    super(VelodyneModel.VLP16, "center");
  }
}
