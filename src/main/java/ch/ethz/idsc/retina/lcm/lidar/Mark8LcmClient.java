// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Device;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;
import idsc.BinaryBlob;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;

/** reference implementation of an lcm client that listens and decodes mark8
 * publications and allows listeners to receive the data
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Mark8LcmClient implements LcmClientInterface, LCMSubscriber {
  private final Mark8Decoder mark8Decoder;
  private final String lidarId;

  public Mark8LcmClient(Mark8Decoder mark8Decoder, String lidarId) {
    this.mark8Decoder = mark8Decoder;
    this.lidarId = lidarId;
  }

  @Override
  public void startSubscriptions() {
    if (mark8Decoder.hasRayListeners())
      LCM.getSingleton().subscribe(Mark8Device.channel(lidarId), this);
  }

  @Override
  public void stopSubscriptions() {
    // TODO Auto-generated method stub
  }

  @Override
  public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins) {
    try {
      BinaryBlob binaryBlob = new BinaryBlob(ins); // <- may throw IOException
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      mark8Decoder.lasers(byteBuffer);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
