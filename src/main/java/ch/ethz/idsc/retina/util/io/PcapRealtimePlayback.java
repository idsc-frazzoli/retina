// code by jph
package ch.ethz.idsc.retina.util.io;

import ch.ethz.idsc.retina.util.time.RealtimeSleeper;

/** slows down parsing of pcap file to a realtime factor */
public class PcapRealtimePlayback implements PcapPacketListener {
  private final RealtimeSleeper realtimeSleeper;

  public PcapRealtimePlayback(double speed) {
    realtimeSleeper = new RealtimeSleeper(speed);
  }

  @Override
  public void pcapPacket(int sec, int usec, byte[] data, int length) {
    realtimeSleeper.now(sec, usec);
  }
}
