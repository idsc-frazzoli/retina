// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.DavisStatics;

/** compiles aps columns and forwards them to a given
 * {@link DavisApsColumnListener} */
public class DavisApsBlockCollector implements DavisApsColumnListener {
  private final int columns = DavisStatics.APS_COLUMNS;
  private final DavisApsBlockListener davisApsBlockListener;
  /** column + COLUMNS * [time + pixels] */
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[2 + columns * DavisApsColumnCompiler.LENGTH]);

  public DavisApsBlockCollector(DavisApsBlockListener davisApsBlockListener) {
    this.davisApsBlockListener = davisApsBlockListener;
    byteBuffer.order(DavisStatics.BYTE_ORDER);
  }

  // public void setListener(DavisApsBlockListener davisApsBlockListener) {
  // this.davisApsBlockListener = davisApsBlockListener;
  // }
  public ByteBuffer byteBuffer() {
    return byteBuffer;
  }

  @Override
  public void column(int x, ByteBuffer columnData) {
    final int xmod = x % columns;
    if (xmod == 0) {
      byteBuffer.position(0);
      byteBuffer.putShort((short) x);
    }
    // the raw data stream may contains gaps, the next put operation is not safe
    // unless we check if there is space remaining
    if (byteBuffer.hasRemaining())
      byteBuffer.put(columnData);
    else
      System.err.println("drop column " + x);
    if (xmod == columns - 1) { // received last
      byteBuffer.position(0);
      davisApsBlockListener.apsBlock(byteBuffer);
    }
  }
}
