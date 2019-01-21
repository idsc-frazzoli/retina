// code by jph
package ch.ethz.idsc.retina.util.io;

@FunctionalInterface
public interface PcapPacketListener {
  /** @param sec number of seconds since the start of 1970, also known as Unix Epoch
   * @param usec ranges from [0, 1, ..., 999999]
   * @param data
   * @param length */
  void pcapPacket(int sec, int usec, byte[] data, int length);
}
