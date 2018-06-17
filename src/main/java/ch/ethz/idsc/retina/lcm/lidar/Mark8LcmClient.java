// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Device;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;

/** reference implementation of an lcm client that listens and decodes mark8
 * publications and allows listeners to receive the data
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Mark8LcmClient extends BinaryLcmClient {
  public final Mark8Decoder mark8Decoder = new Mark8Decoder();
  private final String lidarId;

  public Mark8LcmClient(String lidarId) {
    this.lidarId = lidarId;
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    mark8Decoder.lasers(byteBuffer);
  }

  @Override
  protected String channel() {
    return Mark8Device.channel(lidarId);
  }
}
