// code by jph
package ch.ethz.idsc.gokart.lcm.imu;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;

public class Vmu93xImuLcmClient extends SimpleLcmClient<Vmu931ImuFrameListener> {
  protected Vmu93xImuLcmClient(String channel) {
    super(channel);
  }

  @Override // from BinaryLcmClient
  protected final void messageReceived(ByteBuffer byteBuffer) {
    // System.out.println("msg received");
    Vmu931ImuFrame vmu931ImuFrame = new Vmu931ImuFrame(byteBuffer);
    listeners.forEach(listener -> listener.vmu931ImuFrame(vmu931ImuFrame));
  }
}
