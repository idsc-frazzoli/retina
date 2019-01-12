// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.DavisApsListener;
import ch.ethz.idsc.retina.davis.DavisStatics;
import ch.ethz.idsc.retina.davis._240c.DavisApsEvent;
import ch.ethz.idsc.retina.davis.app.DavisApsCorrection;

/** conceptual sequence aps 1151355 ( 194, 177) 563 aps 1151355 ( 194, 178) 538
 * aps 1151355 ( 194, 179) 538 aps 1151435 ( 195, 0) 612 aps 1151435 ( 195, 1)
 * 615 aps 1151435 ( 195, 2) 618 */
// TODO code is not sufficiently generic due to the magic const
public class CorrectedDavisApsColumnCompiler implements DavisApsListener {
  private static final int LAST_Y = 179;
  private static final int LENGTH = 4 + 180;
  // ---
  private final byte[] data = new byte[LENGTH];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
  private final DavisApsColumnListener davisApsColumnListener;
  private final DavisApsCorrection davisApsCorrection;

  public CorrectedDavisApsColumnCompiler( //
      DavisApsColumnListener davisApsColumnListener, DavisApsCorrection davisApsCorrection) {
    byteBuffer.order(DavisStatics.BYTE_ORDER);
    this.davisApsColumnListener = davisApsColumnListener;
    this.davisApsCorrection = davisApsCorrection;
  }

  @Override
  public void davisAps(DavisApsEvent davisApsEvent) {
    if (davisApsEvent.y == 0) {
      byteBuffer.position(0);
      byteBuffer.putInt(davisApsEvent.time); // prepend time
    }
    // ---
    // subsequent check should not be necessary
    // however, raw data of jaer was observed to contain gaps due to lag/delay
    if (byteBuffer.position() < LENGTH) {
      int ref = davisApsCorrection.nextReference();
      byteBuffer.put(davisApsEvent.grayscale(ref));
    }
    // byteBuffer.put(4 + davisApsEvent.y, davisApsEvent.grayscale());
    // ---
    if (davisApsEvent.y == LAST_Y) { // last
      byteBuffer.position(0);
      davisApsCorrection.reset();
      davisApsColumnListener.column(davisApsEvent.x, byteBuffer);
    }
  }
}
