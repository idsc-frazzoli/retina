// code by jph
package ch.ethz.idsc.retina.util.io;

public interface PcapPacketConsumer {
  void parse(byte[] packet_data, int length);
}
