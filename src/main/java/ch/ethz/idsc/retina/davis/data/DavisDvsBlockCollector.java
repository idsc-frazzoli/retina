// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** encodes an event in 4 bytes (instead of 8 bytes as in aedat) */
public class DavisDvsBlockCollector implements DavisDvsEventListener {
  public static final int MAX_EVENTS = 300;
  public static final int MAX_LENGTH = 2 + 2 + 4 + MAX_EVENTS * 4;
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[MAX_LENGTH]);
  private DavisDvsBlockListener davisDvsBlockListener;

  public DavisDvsBlockCollector() {
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
  }

  int numel = 0;
  int pacid = 0; // TODO increment
  int offset;

  @Override
  public void dvs(DavisDvsEvent davisDvsEvent) {
    if (numel == 0)
      resetTo(davisDvsEvent);
    int exact = davisDvsEvent.time - offset;
    short diff = (short) (exact & 0x7fff);
    if (exact != diff || MAX_EVENTS <= numel) { // TODO also use timeout criterion?
      sendAndReset();
      resetTo(davisDvsEvent);
      exact = davisDvsEvent.time - offset;
      diff = (short) (exact & 0x7fff);
      GlobalAssert.that(exact == 0);
    }
    GlobalAssert.that(exact == diff);
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
    GlobalAssert.that(4 + 4 + numel * 4 == length);
    byteBuffer.position(0);
    byteBuffer.putShort((short) numel); // update numel
    byteBuffer.position(0);
    davisDvsBlockListener.dvsBlockReady(length, byteBuffer);
    numel = 0;
    ++pacid;
  }

  public void setListener(DavisDvsBlockListener davisDvsBlockListener) {
    this.davisDvsBlockListener = davisDvsBlockListener;
  }

  public ByteBuffer byteBuffer() {
    return byteBuffer;
  }
}
