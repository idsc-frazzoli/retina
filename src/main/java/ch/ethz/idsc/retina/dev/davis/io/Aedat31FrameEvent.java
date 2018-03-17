// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** image from camera
 * Documentation taken from
 * https://inilabs.com/support/software/fileformat/#h.w7vjqzw55d5b */
public class Aedat31FrameEvent {
  private final int info;
  private final int frame_start;
  /** "Event-level microsecond End of Frame Capture timestamp.
   * NOTE: This timestamp is considered the primary timestamp
   * for the purpose of ordering packets." */
  private final int frame_end;
  private final int exposure_start;
  private final int exposure_end;
  private final int x_length;
  private final int y_length;
  private final int x_offset;
  private final int y_offset;
  private final BufferedImage bufferedImage;

  public Aedat31FrameEvent(ByteBuffer byteBuffer) {
    info = byteBuffer.getInt();
    frame_start = byteBuffer.getInt();
    frame_end = byteBuffer.getInt();
    exposure_start = byteBuffer.getInt();
    exposure_end = byteBuffer.getInt();
    x_length = byteBuffer.getInt();
    y_length = byteBuffer.getInt();
    x_offset = byteBuffer.getInt();
    y_offset = byteBuffer.getInt();
    {
      int size = x_length * y_length;
      bufferedImage = new BufferedImage(x_length, y_length, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster writableRaster = bufferedImage.getRaster();
      DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
      byte[] bytes = dataBufferByte.getData();
      ByteBuffer imageBuffer = ByteBuffer.wrap(bytes);
      for (int index = 0; index < size; ++index) {
        byte b_lo = byteBuffer.get();
        byte b_hi = byteBuffer.get();
        imageBuffer.put(b_hi);
      }
    }
  }

  public int getTime_us() {
    return frame_end;
  }

  public Scalar getTime() {
    return Quantity.of(RationalScalar.of(frame_end, 1000000), SI.SECOND);
  }

  public BufferedImage getBufferedImage() {
    return bufferedImage;
  }

  public void printInfoLine() {
    System.out.println(frame_start + " " + frame_end);
    System.out.println(x_length + " " + y_length);
    System.out.println(x_offset + " " + y_offset);
  }
}
