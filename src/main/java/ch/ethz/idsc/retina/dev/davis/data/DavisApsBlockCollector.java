// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisStatics;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** compiles aps columns and forwards them to a given {@link DavisApsColumnListener} */
public class DavisApsBlockCollector implements DavisApsColumnListener {
  private final int columns;
  private final int length;
  private final ByteBuffer byteBuffer;
  private DavisApsBlockListener apsBlockListener;

  public DavisApsBlockCollector() {
    this.columns = DavisStatics.APS_COLUMNS;
    length = 2 + columns * 184;
    byte[] data = new byte[length];
    byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(DavisStatics.BYTE_ORDER);
    GlobalAssert.that(240 % columns == 0);
  }

  public void setListener(DavisApsBlockListener apsBlockListener) {
    this.apsBlockListener = apsBlockListener;
  }

  public ByteBuffer byteBuffer() {
    return byteBuffer;
  }

  @Override
  public void column(int x, ByteBuffer columnData) {
    int xmod = x % columns;
    if (xmod == 0) {
      byteBuffer.position(0);
      byteBuffer.putShort((short) x);
    }
    // TODO insert check
    // if raw data stream contains gaps, the next put operation is not safe
    byteBuffer.put(columnData);
    if (xmod == columns - 1) {
      byteBuffer.position(0);
      apsBlockListener.apsBlock(length, byteBuffer);
    }
  }
}
