// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxEvent;
import ch.ethz.idsc.retina.dev.urg04lxug01.Urg04lxEventListener;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class Urg04lxLcmClient implements LcmClientInterface {
  private final String lidarId;
  private final List<Urg04lxEventListener> listeners = new LinkedList<>();

  public Urg04lxLcmClient(String lidarId) {
    this.lidarId = lidarId;
  }

  public void addListener(Urg04lxEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public void startSubscriptions() {
    LCM lcm = LCM.getSingleton();
    lcm.subscribe(Urg04lxDevice.channel(lidarId), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          Urg04lxEvent urg04lxEvent = Urg04lxEvent.fromByteBuffer(byteBuffer);
          listeners.forEach(listener -> listener.range(urg04lxEvent));
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
  }
}
