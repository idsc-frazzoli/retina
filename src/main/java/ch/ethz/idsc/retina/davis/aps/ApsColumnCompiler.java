// code by jph
package ch.ethz.idsc.retina.davis.aps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.davis.DavisApsEventListener;
import ch.ethz.idsc.retina.davis._240c.DavisApsEvent;

/** conceptual sequence
 * aps 1151355 ( 194, 177) 563
 * aps 1151355 ( 194, 178) 538
 * aps 1151355 ( 194, 179) 538
 * aps 1151435 ( 195, 0) 612
 * aps 1151435 ( 195, 1) 615
 * aps 1151435 ( 195, 2) 618 */
// TODO code is not sufficiently generic due to the magic const
public class ApsColumnCompiler implements DavisApsEventListener {
  private static final int LAST_Y = 179;
  private static final int LENGTH = 4 + 180;
  private final byte[] data;
  private final ByteBuffer byteBuffer;
  private final ApsColumnListener columnApsListener;

  public ApsColumnCompiler(ApsColumnListener apsColumnListener) {
    data = new byte[LENGTH];
    byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    this.columnApsListener = apsColumnListener;
  }

  @Override
  public void aps(DavisApsEvent davisApsEvent) {
    if (davisApsEvent.y == 0) {
      byteBuffer.position(0);
      byteBuffer.putInt(davisApsEvent.time); // prepend time
    }
    // ---
    // subsequent check should not be necessary
    // however, raw data of jaer was observed to contain gaps due to lag/delay
    if (byteBuffer.position() < LENGTH)
      byteBuffer.put(davisApsEvent.grayscale());
    // byteBuffer.put(4 + davisApsEvent.y, davisApsEvent.grayscale());
    // ---
    if (davisApsEvent.y == LAST_Y) { // last
      byteBuffer.position(0);
      columnApsListener.column(davisApsEvent.x, byteBuffer);
    }
  }
}
