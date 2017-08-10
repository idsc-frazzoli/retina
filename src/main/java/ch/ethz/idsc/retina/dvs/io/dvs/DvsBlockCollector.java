// code by jph
package ch.ethz.idsc.retina.dvs.io.dvs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.davis.DvsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis._240c.DvsDavisEvent;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** encodes an event in 4 bytes (instead of 8 bytes as in aedat) */
public class DvsBlockCollector implements DvsDavisEventListener {
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[34096]);
  private DvsBlockListener dvsBlockListener;

  public DvsBlockCollector() {
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
  }

  int numel = 0;
  int pacid = 0; // TODO increment
  int offset;

  @Override
  public void dvs(DvsDavisEvent dvsDavisEvent) {
    if (numel == 0)
      resetTo(dvsDavisEvent);
    int exact = dvsDavisEvent.time - offset;
    short diff = (short) (exact & 0x7fff);
    if (exact != diff || 300 <= numel) { // TODO also use timeout criterion?
      sendAndReset();
      resetTo(dvsDavisEvent);
      exact = dvsDavisEvent.time - offset;
      diff = (short) (exact & 0x7fff);
      GlobalAssert.that(exact == 0);
    }
    GlobalAssert.that(exact == diff);
    diff <<= 1;
    diff |= dvsDavisEvent.i;
    byteBuffer.putShort(diff);
    byteBuffer.put((byte) dvsDavisEvent.x);
    byteBuffer.put((byte) dvsDavisEvent.y);
    ++numel;
  }

  private void resetTo(DvsDavisEvent dvsDavisEvent) {
    offset = dvsDavisEvent.time;
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
    dvsBlockListener.dvsBlockReady(length);
    numel = 0;
    ++pacid;
  }

  public void setListener(DvsBlockListener dvsBlockListener) {
    this.dvsBlockListener = dvsBlockListener;
  }

  public ByteBuffer byteBuffer() {
    return byteBuffer;
  }
}
