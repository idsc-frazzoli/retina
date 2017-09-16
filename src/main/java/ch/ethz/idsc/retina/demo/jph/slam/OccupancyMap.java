// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.BinCounts;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Norm;

public class OccupancyMap implements LidarRayBlockListener {
  private static final int WIDTH = 1024;
  private static final float METER_TO_PIXEL = 50;
  // ---
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_BYTE_GRAY);
  private final Graphics graphics = bufferedImage.getGraphics();
  private final byte[] bytes;
  private Tensor global;
  private Tensor pose;
  private Tensor center;

  public OccupancyMap() {
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
    global = IdentityMatrix.of(3);
    pose = IdentityMatrix.of(3);
    center = IdentityMatrix.of(3);
    center.set(RealScalar.of(512), 0, 2);
    center.set(RealScalar.of(512), 1, 2);
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    GlobalAssert.that(lidarRayBlockEvent.dimensions == 2);
    int n = lidarRayBlockEvent.floatBuffer.limit() / 2;
    Tensor points = Tensors.vector(i -> Tensors.vector( //
        lidarRayBlockEvent.floatBuffer.get(), //
        lidarRayBlockEvent.floatBuffer.get(), //
        1), n);
    System.out.println(Dimensions.of(points));
    Tensor nrm = Tensor.of(Differences.of(points).stream().map(Norm._2::ofVector));
    System.out.println(nrm.stream().reduce(Min::of).get());
    Tensor bins = BinCounts.of(nrm, RealScalar.of(0.001));
    System.out.println(bins);
    Tensor rep = Tensor.of(points.stream().map(row -> center.dot(row)));
    for (Tensor point : rep) {
      // System.out.println(point);
      int x = point.Get(0).number().intValue(); // TODO not a good method because of rounding towards 0
      if (0 <= x && x < WIDTH) {
        int y = point.Get(1).number().intValue();
        if (0 <= y && y < WIDTH) {
          graphics.setColor(new Color(128, 128, 128, 255));
          graphics.fillRect(x - 1, y - 1, 3, 3);
          // bufferedImage.setRGB(x, y, 255);
          bytes[x + WIDTH * y] = (byte) 255;
        }
      }
    }
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }
}
