// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;

public class Vmu932ImuLcmClient extends Vmu93xImuLcmClient {
  public Vmu932ImuLcmClient() {
    super(GokartLcmChannel.VMU932_AG);
  }
}
