// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.quanergy.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

/** reference implementation of an lcm client that listens and decodes
 * mark8 publications and allows listeners to receive the data
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Mark8LcmClient implements LcmClientInterface {
  public final Mark8Decoder mark8Decoder;
  private final String lidarId;

  private Mark8LcmClient(Mark8Decoder mark8Decoder, String lidarId) {
    this.mark8Decoder = mark8Decoder;
    this.lidarId = lidarId;
  }

  @Override
  public void startSubscriptions() {
    LCM lcm = LCM.getSingleton();
    // if (decoder.hasListeners())
    lcm.subscribe("mark8." + lidarId + ".ray", new LCMSubscriber() {
      @Override
      public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
        try {
          BinaryBlob binaryBlob = new BinaryBlob(ins);
          ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
          byteBuffer.order(ByteOrder.BIG_ENDIAN); // native encoding of mark8 sensor is big endian
          mark8Decoder.lasers(byteBuffer);
        } catch (IOException exception) {
          exception.printStackTrace();
        }
      }
    });
  }
}
