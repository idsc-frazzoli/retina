// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;

public class Vmu931ImuLcmClient extends Vmu93xImuLcmClient {
  public Vmu931ImuLcmClient() {
    super(GokartLcmChannel.VMU931_AG);
  }
}
