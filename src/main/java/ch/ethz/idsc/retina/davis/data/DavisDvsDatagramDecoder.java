// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** decoder of davis240C event packets
 * a single packet may contain up to ~300 events
 * each event in the packet is passed to each listener */
public class DavisDvsDatagramDecoder {
  private final List<DavisDvsListener> listeners = new CopyOnWriteArrayList<>();

  public void addDvsListener(DavisDvsListener listener) {
    listeners.add(listener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  private short pacid_next = -1;
  private int missed = -1;
  private int missed_print = 0;
  private long total;

  public void decode(ByteBuffer byteBuffer) {
    byteBuffer.position(0);
    int numel = byteBuffer.getShort(); // number of events in packet
    short pacid = byteBuffer.getShort(); // running id of packet
    if (pacid_next != pacid) {
      ++missed;
      if (missed != 0)
        System.err.println("dvs packet missing");
    }
    int offset = byteBuffer.getInt();
    for (int count = 0; count < numel; ++count) {
      final int misc = byteBuffer.getShort() & 0xffff;
      final int time = offset + (misc >> 1);
      final int x = byteBuffer.get() & 0xff;
      final int y = byteBuffer.get() & 0xff;
      final int i = misc & 1;
      DavisDvsEvent davisDvsEvent = new DavisDvsEvent(time, x, y, i);
      listeners.forEach(listener -> listener.davisDvs(davisDvsEvent));
    }
    ++total;
    if (total % 1000 == 0 && missed_print != missed) {
      missed_print = missed;
      double percent = missed * 100.0 / total;
      System.out.println("dvs loss = " + missed + "/" + total + String.format(" = %4.2f%%", percent));
    }
    pacid_next = ++pacid;
  }

  public void removeDvsListener(DavisDvsListener davisDvsListener) {
    listeners.remove(davisDvsListener);
  }
}
