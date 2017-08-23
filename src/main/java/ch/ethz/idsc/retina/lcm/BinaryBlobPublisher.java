// code by jph
package ch.ethz.idsc.retina.lcm;

import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class BinaryBlobPublisher implements PcapPacketConsumer {
  private final LCM lcm = LCM.getSingleton();
  private final String channel;

  public BinaryBlobPublisher(String channel) {
    this.channel = channel;
  }

  @Override
  public void parse(byte[] packet_data, int length) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = packet_data;
    lcm.publish(channel, binaryBlob);
  }
}
