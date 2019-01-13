// code by jph
package ch.ethz.idsc.retina.dvs.io.dat;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.dvs.supply.DvsEventSupplier;

/** very efficient for postprocessing: playback rates of up to 43M [events/sec]
 * 
 * The events are sequentially stored in a binary file. Each event is encoded
 * into 8 bytes, where the first 4 bytes stores the absolute time-stamp in
 * microseconds and the last 4 bytes stores the event-polarity (1 for positive,
 * 0 for negative) and the x and y position in the image.
 * 
 * The latter bytes can be processed by using the the following bit-masks:
 * 
 * image_position_x = data & 0x000001FF
 * 
 * image_position_y = (data & 0x0001FE00) >> 9
 * 
 * polarity = (data & 0x00020000) >> 17,
 * 
 * where data is an integer, which contains the last 4 bytes of an event.
 * 
 * 2^31/1000000 == 2147 sec == 35 minutes
 * 
 * http://wp.doc.ic.ac.uk/pb2114/datasets/ */
public class DatFileSupplier implements DvsEventSupplier, AutoCloseable {
  private static final int MASK_X = 0x1ff;
  private static final int MASK_Y = 0x1fe00;
  private static final int MASK_I = 0x00020000;
  // ---
  private final Dimension dimension;
  private final byte[] bytes = new byte[8 * StaticHelper.BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
  private final InputStream inputStream;
  private int available = 0;

  public DatFileSupplier(File file, Dimension dimension) throws Exception {
    inputStream = new FileInputStream(file);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    this.dimension = dimension;
  }

  @Override
  public DvsEvent next() throws Exception {
    if (available == 0) {
      available += inputStream.read(bytes, 0, bytes.length);
      byteBuffer.position(0);
    }
    int time = byteBuffer.getInt(); // microseconds
    int ixy = byteBuffer.getInt();
    int x = ixy & MASK_X;
    int y = (ixy & MASK_Y) >> StaticHelper.SHIFT_Y;
    int i = (ixy & MASK_I) >> StaticHelper.SHIFT_I;
    available -= 8;
    return new DvsEvent(time, x, y, i);
  }

  @Override
  public Dimension dimension() {
    return dimension;
  }

  @Override
  public void close() throws Exception {
    System.out.println("close called");
    inputStream.close();
  }
}
