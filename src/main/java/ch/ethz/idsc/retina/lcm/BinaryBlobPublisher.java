// code by jph
package ch.ethz.idsc.retina.lcm;

import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

/** universal implementation that publishes the lcm type {@link BinaryBlob}
 * 
 * known use cases: Hdl32e, Vlp16, Urg04lxug01 */
public class BinaryBlobPublisher implements ByteArrayConsumer {
  private final LCM lcm = LCM.getSingleton();
  private final String channel;

  public BinaryBlobPublisher(String channel) {
    this.channel = channel;
  }

  @Override
  public void accept(byte[] packet_data, int length) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = packet_data;
    lcm.publish(channel, binaryBlob);
  }
}
