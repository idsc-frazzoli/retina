// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;

/** primary imu */
public class Vmu931LcmServerModule extends Vmu931LcmServerBase {
  public Vmu931LcmServerModule() {
    super("/dev/ttyACM0", GokartLcmChannel.VMU931_AG);
  }
}
