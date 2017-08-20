// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dev.davis.DavisApsEventListener;

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
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    this.davisApsColumnListener = davisApsColumnListener;
  }
}
