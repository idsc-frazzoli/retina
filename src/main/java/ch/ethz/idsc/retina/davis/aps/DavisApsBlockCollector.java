// code by jph
package ch.ethz.idsc.retina.davis.aps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** compiles aps columns and forwards them to a given {@link DavisApsColumnListener} */
public class DavisApsBlockCollector implements DavisApsColumnListener {
  private final int columns;
  private final int length;
  private final ByteBuffer byteBuffer;
  private DavisApsBlockListener apsBlockListener;

  /** for an image of width == 240
   * column is in {2, 3, 4, 5, 6, 8, 10, ... }
   * FactorInteger[240] == {{2, 4}, {3, 1}, {5, 1}}
   * 
   * @param columns has to divide image width */
  public DavisApsBlockCollector(int columns) {
    this.columns = columns;
    length = 2 + columns * 184;
    byte[] data = new byte[length];
    byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
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
