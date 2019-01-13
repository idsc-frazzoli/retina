// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import ch.ethz.idsc.retina.lidar.VelodyneModel;

public class Hdl32eLcmServerModule extends VelodyneLcmServerModule {
  public Hdl32eLcmServerModule() {
    super(VelodyneModel.HDL32E, "center");
  }
}
