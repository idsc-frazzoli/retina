// code by jph
package ch.ethz.idsc.retina.davis.io;

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
  /** Holds information on the frame event.
   * Validity mark.
   * Color channels number, to track multiple color channels,
   * for example RGB. Valid values are:
   * GRAYSCALE(1), RGB(3) and RGBA(4) */
  private final int info;
  /** Event-level microsecond Start of Frame Capture timestamp. */
  private final int frameStart;
  /** "Event-level microsecond End of Frame Capture timestamp.
   * NOTE: This timestamp is considered the primary timestamp
   * for the purpose of ordering packets." */
  private final int frameEnd;
  /** Event-level microsecond Start of Exposure timestamp. */
  private final int exposureStart;
  /** Event-level microsecond End of Exposure timestamp. */
  private final int exposureEnd;
  /** X axis length in pixels. */
  private final int x_length;
  /** Y axis length in pixels. */
  private final int y_length;
  /** X axis position (upper left offset) in pixels. */
  private final int x_offset;
  /** Y axis position (upper left offset) in pixels. */
  private final int y_offset;
  private final BufferedImage bufferedImage;

  public Aedat31FrameEvent(ByteBuffer byteBuffer) {
    info = byteBuffer.getInt();
    frameStart = byteBuffer.getInt();
    frameEnd = byteBuffer.getInt();
    exposureStart = byteBuffer.getInt();
    exposureEnd = byteBuffer.getInt();
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
        byteBuffer.get(); // discard lowest bits
        byte b_hi = byteBuffer.get();
        imageBuffer.put(b_hi);
      }
    }
  }

  public int getTime_us() {
    return frameEnd;
  }

  public Scalar getTime() {
    return Quantity.of(RationalScalar.of(frameEnd, 1000000), SI.SECOND);
  }

  public BufferedImage getBufferedImage() {
    return bufferedImage;
  }

  public int getInfo() {
    return info;
  }

  public int getExposureStart() {
    return exposureStart;
  }

  public int getExposureEnd() {
    return exposureEnd;
  }

  public void printInfoLine() {
    System.out.println(frameStart + " " + frameEnd);
    System.out.println(x_length + " " + y_length);
    System.out.println(x_offset + " " + y_offset);
  }
}
