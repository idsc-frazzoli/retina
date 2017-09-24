// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class Urg04lxLcmClient implements LcmClientInterface, LCMSubscriber {
  public final Urg04lxDecoder urg04lxDecoder = new Urg04lxDecoder();
  private final String lidarId;

  public Urg04lxLcmClient(String lidarId) {
    this.lidarId = lidarId;
  }

  @Override
  public void startSubscriptions() {
    LCM.getSingleton().subscribe(name(), this);
  }

  @Override
  public void stopSubscriptions() {
    LCM.getSingleton().unsubscribe(name(), this);
  }

  private String name() {
    return Urg04lxDevice.channel(lidarId);
  }

  @Override
  public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins);
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      urg04lxDecoder.lasers(byteBuffer);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
