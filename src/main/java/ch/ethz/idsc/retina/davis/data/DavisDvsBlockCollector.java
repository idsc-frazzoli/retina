// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis.DavisStatics;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** encodes an event in 4 bytes (instead of 8 bytes as in AEDAT2.0) */
public class DavisDvsBlockCollector implements DavisDvsListener {
  public static final int MAX_EVENTS = 300;
  public static final int MAX_LENGTH = 2 + 2 + 4 + MAX_EVENTS * 4;
  // ---
  private final DavisDvsBlockListener davisDvsBlockListener;
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[MAX_LENGTH]);

  public DavisDvsBlockCollector(DavisDvsBlockListener davisDvsBlockListener) {
    this.davisDvsBlockListener = davisDvsBlockListener;
    byteBuffer.order(DavisStatics.BYTE_ORDER);
  }

  private int numel = 0;
  private int pacid = 0;
  private int offset;

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (numel == 0) // only true for first event
      resetTo(davisDvsEvent);
    int exact = davisDvsEvent.time - offset;
    short diff = (short) (exact & 0x7fff);
    if (exact != diff || MAX_EVENTS <= numel) {
      sendAndReset();
      resetTo(davisDvsEvent);
      exact = davisDvsEvent.time - offset; // == 0
      diff = (short) (exact & 0x7fff); // == 0
    }
    // GlobalAssert.that(exact == diff);
    diff <<= 1;
    diff |= davisDvsEvent.i;
    byteBuffer.putShort(diff);
    byteBuffer.put((byte) davisDvsEvent.x);
    byteBuffer.put((byte) davisDvsEvent.y);
    ++numel;
  }

  private void resetTo(DavisDvsEvent davisDvsEvent) {
    offset = davisDvsEvent.time;
    // first two bytes are reserved for count
    byteBuffer.position(2);
    byteBuffer.putShort((short) pacid);
    byteBuffer.putInt(offset);
  }

  private void sendAndReset() {
    int length = byteBuffer.position();
    // GlobalAssert.that(4 + 4 + numel * 4 == length);
    byteBuffer.position(0);
    byteBuffer.putShort((short) numel); // update numel
    byteBuffer.position(0);
    davisDvsBlockListener.dvsBlock(length, byteBuffer);
    numel = 0;
    ++pacid;
  }
}
