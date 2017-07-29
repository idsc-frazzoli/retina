// code by jph
package ch.ethz.idsc.retina.dvs.io.dat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.dvs.digest.DvsEventDigest;

// TODO constants are redundant
public class DatFileDigest implements DvsEventDigest, AutoCloseable {
  private static final int BUFFER_SIZE = 512;
  private static final int SHIFT_Y = 9;
  private static final int SHIFT_I = 17;
  // ---
  private final byte[] bytes = new byte[8 * BUFFER_SIZE];
  private final OutputStream outputStream;
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

  public DatFileDigest(File file) throws Exception {
    outputStream = new FileOutputStream(file);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
  }

  @Override
  public void digest(DvsEvent dvsEvent) {
    if (byteBuffer.remaining() == 0) {
      try {
        outputStream.write(bytes);
        outputStream.flush(); // TODO not final design
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      byteBuffer.position(0);
    }
    if ((dvsEvent.time_us & 0xffffffffL) != dvsEvent.time_us)
      throw new RuntimeException();
    byteBuffer.putInt((int) dvsEvent.time_us);
    int mx = dvsEvent.x;
    int my = dvsEvent.y << SHIFT_Y;
    int mi = dvsEvent.i << SHIFT_I;
    byteBuffer.putInt(mi | my | mx);
  }

  @Override
  public void close() throws Exception {
    outputStream.close();
  }
}
