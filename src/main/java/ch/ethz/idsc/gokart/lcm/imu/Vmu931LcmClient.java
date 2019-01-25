// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931Frame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931FrameListener;

public class Vmu931LcmClient extends SimpleLcmClient<Vmu931FrameListener> {
  public Vmu931LcmClient() {
    super(GokartLcmChannel.VMU931_AG);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    Vmu931Frame vmu931Frame = new Vmu931Frame(byteBuffer);
    listeners.forEach(listener -> listener.vmu931Frame(vmu931Frame));
  }
}
