// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class DavisDvsDatagramDecoder {
  private final List<DavisDvsEventListener> listeners = new LinkedList<>();

  public void addListener(DavisDvsEventListener davisDvsEventListener) {
    listeners.add(davisDvsEventListener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  private short pacid_next = -1;
  private int missed;
  private int missed_print;
  private long total;

  public void decode(ByteBuffer byteBuffer) {
    byteBuffer.position(0);
    // TODO check consistency
    int numel = byteBuffer.getShort(); // number of events in packet
    short pacid = byteBuffer.getShort(); // running id of packet
    if (pacid_next != pacid)
      ++missed;
    // System.err.println("dvs packet missing");
    int offset = byteBuffer.getInt();
    for (int count = 0; count < numel; ++count) {
      final int misc = byteBuffer.getShort() & 0xffff;
      final int time = offset + (misc >> 1);
      final int x = byteBuffer.get() & 0xff;
      final int y = byteBuffer.get() & 0xff;
      final int i = misc & 1;
      DavisDvsEvent davisDvsEvent = new DavisDvsEvent(time, x, y, i);
      listeners.forEach(listener -> listener.dvs(davisDvsEvent));
    }
    ++total;
    if (total % 1000 == 0 && missed_print != missed) {
      missed_print = missed;
      double percent = missed * 100.0 / (double) total;
      System.out.println("dvs loss = " + missed + "/" + total + String.format(" = %4.2f%%", percent));
    }
    pacid_next = ++pacid;
  }
}
