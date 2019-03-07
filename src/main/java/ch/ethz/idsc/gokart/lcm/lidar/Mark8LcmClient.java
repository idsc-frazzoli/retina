// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.lidar.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.lidar.mark8.Mark8Device;

/** reference implementation of an lcm client that listens and decodes mark8
 * publications and allows listeners to receive the data
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Mark8LcmClient extends BinaryLcmClient {
  public final Mark8Decoder mark8Decoder = new Mark8Decoder();

  public Mark8LcmClient(String lidarId) {
    super(Mark8Device.channel(lidarId));
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    mark8Decoder.lasers(byteBuffer);
  }
}
