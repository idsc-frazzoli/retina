// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;

/** primary imu
 * mandatory for operation */
public class Vmu931LcmServerModule extends Vmu93xLcmServerBase {
  private static final String PORT = "/dev/ttyACM0";

  public Vmu931LcmServerModule() {
    super(PORT, GokartLcmChannel.VMU931_AG);
  }
}
