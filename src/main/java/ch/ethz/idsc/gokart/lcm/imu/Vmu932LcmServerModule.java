// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;

/** primary imu */
public class Vmu932LcmServerModule extends Vmu931LcmServerBase {
  public Vmu932LcmServerModule() {
    super("/dev/ttyACM1", GokartLcmChannel.VMU932_AG);
  }
}
