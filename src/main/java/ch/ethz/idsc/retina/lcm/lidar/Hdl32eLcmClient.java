// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketDecoder;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

public class Hdl32eLcmClient implements LcmClientInterface {
  private final String lidarId;
  public final Hdl32eFiringPacketDecoder hdl32eFiringPacketDecoder = new Hdl32eFiringPacketDecoder();

  public Hdl32eLcmClient(String lidarId) {
    this.lidarId = lidarId;
  }

  @Override
  public void subscribe() {
    LCM lcm = LCM.getSingleton();
    lcm.subscribe(Hdl32eLcmChannels.firing(lidarId), new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          hdl32eFiringPacketDecoder.lasers(byteBuffer);
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
          // System.out.println("pos" + binaryBlob.data_length);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
  }
}
