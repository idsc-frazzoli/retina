// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.alg.slam.Se2MultiresSamples;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class SlamDunk {
  private final byte[] bytes;
  private final int WIDTH;
  private Se2MultiresSamples se2MultiresSamples;

  public SlamDunk(BufferedImage bufferedImage) {
    WIDTH = bufferedImage.getWidth();
    // HEIGHT = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
  }

  public void set(Se2MultiresSamples se2MultiresSamples) {
    this.se2MultiresSamples = se2MultiresSamples;
  }

  int cmp = -1;

  /** @return sum of all grayscale color values of the pixels in the map
   * that coincide with a lidar sample. the maximum possible value is the
   * number of samples multiplied by 255 */
  public int getMatchQuality() {
    return cmp;
  }

  public Tensor fit(GeometricLayer geometricLayer, List<Tensor> list) {
    Tensor result = IdentityMatrix.of(3);
    int pushed = 0;
    for (int level = 0; level < se2MultiresSamples.levels(); ++level) {
      cmp = -1;
      Tensor best = null;
      for (Tensor delta : se2MultiresSamples.level(level)) {
        geometricLayer.pushMatrix(delta);
        int eval = 0;
        for (Tensor pnts : list)
          for (Tensor x : pnts) {
            Point2D point2D = geometricLayer.toPoint2D(x);
            eval += evaluate(point2D);
          }
        if (cmp < eval) {
          best = delta;
          cmp = eval;
        }
        geometricLayer.popMatrix();
      }
      if (Objects.nonNull(best)) {
        geometricLayer.pushMatrix(best);
        result = result.dot(best);
        ++pushed;
      } else
        System.err.println("stack size mismatch");
    }
    for (int count = 0; count < pushed; ++count)
      geometricLayer.popMatrix();
    return result;
  }

  private int evaluate(Point2D point2D) {
    int sum = 0;
    int x = (int) point2D.getX();
    if (0 <= x && x < WIDTH) {
      int y = (int) point2D.getY();
      if (0 <= y && y < WIDTH)
        sum += bytes[x + WIDTH * y] & 0xff;
    }
    return sum;
  }
}
