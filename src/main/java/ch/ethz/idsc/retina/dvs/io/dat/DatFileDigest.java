// code by jph
package ch.ethz.idsc.retina.dvs.io.dat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.dvs.digest.DvsEventDigest;

public class DatFileDigest implements DvsEventDigest, AutoCloseable {
  private final byte[] bytes = new byte[8 * StaticHelper.BUFFER_SIZE];
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
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      byteBuffer.position(0);
    }
    if ((dvsEvent.time_us & 0xffffffffL) != dvsEvent.time_us)
      throw new RuntimeException();
    byteBuffer.putInt((int) dvsEvent.time_us);
    int mx = dvsEvent.x;
    int my = dvsEvent.y << StaticHelper.SHIFT_Y;
    int mi = dvsEvent.i << StaticHelper.SHIFT_I;
    byteBuffer.putInt(mi | my | mx);
  }

  @Override
  public void close() throws Exception {
    outputStream.flush();
    outputStream.close();
  }
}
