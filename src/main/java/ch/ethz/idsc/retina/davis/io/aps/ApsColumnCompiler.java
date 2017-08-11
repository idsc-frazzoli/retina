// code by jph
package ch.ethz.idsc.retina.davis.io.aps;

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
  private final byte[] data;
  private final ByteBuffer byteBuffer;
  private final ApsColumnListener columnApsListener;

  public ApsColumnCompiler(ApsColumnListener columnApsListener) {
    data = new byte[4 + 180];
    byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    this.columnApsListener = columnApsListener;
  }

  @Override
  public void aps(DavisApsEvent apsDavisEvent) {
    if (apsDavisEvent.y == 0) {
      byteBuffer.position(0);
      byteBuffer.putInt(apsDavisEvent.time); // prepend time
    }
    // ---
    byteBuffer.put(apsDavisEvent.grayscale());
    // ---
    if (apsDavisEvent.y == 179) { // last
      byteBuffer.position(0);
      columnApsListener.column(apsDavisEvent.x, byteBuffer);
    }
  }
}
