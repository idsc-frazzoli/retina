// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxDecoder;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxDevice;

/** listen to specific urg04lx channel and decode urg messages
 * 
 * Hint:
 * 1) create new Urg04lxLcmClient("front") // modify name of lidar if necessary
 * 2) add all ray listeners to the urg04lxDecoder
 * 3) call startSubscriptions() */
public class Urg04lxLcmClient extends BinaryLcmClient {
  public final Urg04lxDecoder urg04lxDecoder = new Urg04lxDecoder();

  public Urg04lxLcmClient(String lidarId) {
    super(Urg04lxDevice.channel(lidarId));
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    urg04lxDecoder.lasers(byteBuffer);
  }
}
