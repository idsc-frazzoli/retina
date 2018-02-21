// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.gokart.core.slam.SlamScore;

public class ImageScore implements SlamScore {
  /** @param bufferedImage grayscale image in byte array encoding, each pixel corresponds to one byte */
  public static SlamScore of(BufferedImage bufferedImage) {
    return new ImageScore(bufferedImage);
  }

  // ---
  private final byte[] bytes;
  private final int width;
  private final int height;

  private ImageScore(BufferedImage bufferedImage) {
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
  }

  @Override // from SlamScore
  public int evaluate(Point2D point2D) {
    int x = (int) point2D.getX();
    if (0 <= x && x < width) {
      int y = (int) point2D.getY();
      if (0 <= y && y < height)
        return bytes[x + width * y] & 0xff;
    }
    return 0;
  }
}
