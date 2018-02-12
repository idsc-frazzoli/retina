// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.UniformResample;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/** first localization algorithm deployed on the gokart.
 * the iterative method is used since December 2017.
 * 
 * the localization algorithm relies on a map that encodes
 * free space and obstacles.
 * 
 * confirmed to work well at speeds of up to 2[m/s] following
 * the oval trajectory in the dubendorf hangar */
public class SlamDunk {
  private final byte[] bytes;
  private final int width;
  private final int height;

  /** @param bufferedImage grayscale image in byte array encoding, each pixel corresponds to one byte */
  public SlamDunk(BufferedImage bufferedImage) {
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
  }

  /** the list of points is typically provided by {@link UniformResample}
   * 
   * @param se2MultiresSamples
   * @param geometricLayer
   * @param points with dimension n x 2 {{px_1, py_1}, ..., {px_n, py_n}}
   * @return */
  public SlamResult fit(Se2MultiresSamples se2MultiresSamples, GeometricLayer geometricLayer, Tensor points) {
    Tensor result = IdentityMatrix.of(3);
    int pushed = 0;
    int cmp = -1;
    for (int level = 0; level < se2MultiresSamples.levels(); ++level) {
      cmp = -1;
      Tensor best = null;
      for (Tensor delta : se2MultiresSamples.level(level)) { // TODO can do in parallel
        geometricLayer.pushMatrix(delta);
        int eval = points.stream().map(geometricLayer::toPoint2D) //
            .mapToInt(this::evaluate).sum();
        if (cmp < eval) {
          best = delta;
          cmp = eval;
        }
        geometricLayer.popMatrix();
      }
      geometricLayer.pushMatrix(best); // manifest for next level
      result = result.dot(best);
      ++pushed;
    }
    for (int count = 0; count < pushed; ++count)
      geometricLayer.popMatrix();
    return new SlamResult(result, RationalScalar.of(cmp, points.length() * 255));
  }

  /** @param point2D
   * @return integer in the range [0, 1, ..., 255] */
  private int evaluate(Point2D point2D) {
    int x = (int) point2D.getX();
    if (0 <= x && x < width) {
      int y = (int) point2D.getY();
      if (0 <= y && y < height)
        return bytes[x + width * y] & 0xff;
    }
    return 0;
  }
}
