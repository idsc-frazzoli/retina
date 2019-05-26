// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;

/** backup imu */
public class Vmu932LcmServerModule extends Vmu93xLcmServerBase {
  private static final String PORT = "/dev/ttyACM1";

  public Vmu932LcmServerModule() {
    super(PORT, GokartLcmChannel.VMU932_AG);
  }
}
