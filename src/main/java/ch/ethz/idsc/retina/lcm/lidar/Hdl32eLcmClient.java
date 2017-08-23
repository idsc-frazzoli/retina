// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;

import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class Hdl32eLcmClient implements LcmClientInterface {
  private final LCM lcm = LCM.getSingleton();
  private final String lidarId;

  public Hdl32eLcmClient(String lidarId) {
    this.lidarId = lidarId;
  }

  @Override
  public void subscribe() {
    lcm.subscribe(Hdl32eLcmChannels.firing(lidarId), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          System.out.println("fir" + binaryBlob.data_length);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
    lcm.subscribe(Hdl32eLcmChannels.positioning(lidarId), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          System.out.println("pos" + binaryBlob.data_length);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
  }

  public static void main(String[] args) throws Exception {
    Hdl32eLcmClient client = new Hdl32eLcmClient("center");
    client.subscribe();
    Thread.sleep(4000);
  }
}
