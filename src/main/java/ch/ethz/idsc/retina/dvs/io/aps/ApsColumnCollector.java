// code by jph
package ch.ethz.idsc.retina.dvs.io.aps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** compiles aps columns and forwards them to a given {@link ApsColumnListener} */
public class ApsColumnCollector implements ApsColumnListener {
  private final int columns;
  private final ByteBuffer byteBuffer;
  private ApsBlockListener blockApsListener;

  /** for an image of width == 240
   * column is in {2, 3, 4, 5, 6, 8, 10, ... }
   * FactorInteger[240] == {{2, 4}, {3, 1}, {5, 1}}
   * 
   * @param columns has to divide image width */
  public ApsColumnCollector(int columns) {
    this.columns = columns;
    byte[] data = new byte[2 + columns * 184];
    byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    GlobalAssert.that(240 % columns == 0);
  }

  public void setListener(ApsBlockListener blockApsListener) {
    this.blockApsListener = blockApsListener;
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
    byteBuffer.put(columnData);
    if (xmod == columns - 1) {
      GlobalAssert.that(byteBuffer.remaining() == 0);
      blockApsListener.block();
    }
  }
}
