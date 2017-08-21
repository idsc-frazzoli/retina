// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisApsEventListener;
import ch.ethz.idsc.retina.dev.davis.DavisStatics;

public abstract class DavisApsColumnCompiler implements DavisApsEventListener {
  static final int LAST_Y = 179;
  static final int LENGTH = 4 + 180;
  // ---
  final byte[] data;
  final ByteBuffer byteBuffer;
  final DavisApsColumnListener davisApsColumnListener;

  public DavisApsColumnCompiler(DavisApsColumnListener davisApsColumnListener) {
    data = new byte[LENGTH];
    byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(DavisStatics.BYTE_ORDER);
    this.davisApsColumnListener = davisApsColumnListener;
  }
}
