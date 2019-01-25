// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;

public class Vmu931ImuLcmClient extends SimpleLcmClient<Vmu931ImuFrameListener> {
  public Vmu931ImuLcmClient() {
    super(GokartLcmChannel.VMU931_AG);
  }

  @Override // from BinaryLcmClient
  protected void messageReceived(ByteBuffer byteBuffer) {
    // System.out.println("msg received");
    Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
    listeners.forEach(listener -> listener.vmu931ImuFrame(vmu931ImuFrame));
  }
}
