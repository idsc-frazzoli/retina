// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.app.UniformResample;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.Stopwatch;
import ch.ethz.idsc.retina.util.math.Se2Sampler;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class OccupancyMap implements LidarRayBlockListener {
  public static final int WIDTH = 1024;
  public static final float METER_TO_PIXEL = 50;
  public static final Scalar M2PIX = RealScalar.of(METER_TO_PIXEL);
  // ---
  private final UniformResample uniformResample = new UniformResample(RealScalar.of(43), RealScalar.of(0.04));
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_BYTE_GRAY);
  private final Graphics graphics = bufferedImage.getGraphics();
  private final byte[] bytes;
  private Tensor global;
  private Tensor pose;
  // private Tensor center;
  private boolean optimize = false;
  private final List<OccupancyMapListener> listeners = new LinkedList<>();

  public OccupancyMap() {
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
    global = IdentityMatrix.of(3);
    pose = IdentityMatrix.of(3);
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    GlobalAssert.that(lidarRayBlockEvent.dimensions == 2);
    // int n = ;
    Tensor points = Tensors.vector(i -> Tensors.vector( //
        lidarRayBlockEvent.floatBuffer.get(), //
        lidarRayBlockEvent.floatBuffer.get()), lidarRayBlockEvent.size());
    points = uniformResample.apply(points).multiply(M2PIX);
    points.stream().forEach(row -> row.append(RealScalar.ONE));
    System.out.println(lidarRayBlockEvent.size() + " -> " + Dimensions.of(points));
    if (optimize) {
      // TODO optimize
      Stopwatch stp = Stopwatch.started();
      Scalar ang = RealScalar.of(2 * Math.PI / 180);
      Scalar shf = RealScalar.of(0.03);
      // Tensor best = IdentityMatrix.of(3);
      for (int iterate = 0; iterate < 2; ++iterate) {
        // ---
        Tensor next = null;
        int cmp = 0;
        for (int x = -1; x <= 1; ++x) {
          for (int y = -1; y <= 1; ++y) {
            for (int t = -1; t <= 1; ++t) {
              Tensor test = pose.dot(Se2Sampler.get( //
                  ang.multiply(RealScalar.of(t)), //
                  shf.multiply(RealScalar.of(x)), //
                  shf.multiply(RealScalar.of(y))));
              Tensor evl = Tensor.of(points.stream().map(row -> test.dot(row)));
              int ret = evaluate(evl);
              if (cmp < ret) {
                next = test;
                cmp = ret;
              }
              // System.out.println(Tensors.vector(x, y, t) + " " + ret);
            }
          }
        }
        pose = next;
        // ---
        ang = ang.multiply(RealScalar.of(.6));
        shf = shf.multiply(RealScalar.of(.6));
      }
      System.out.println(Pretty.of(pose));
      System.out.println("optimize " + stp.display_seconds());
    }
    optimize |= true;
    Tensor reps = Tensor.of(points.stream().map(row -> pose.dot(row)));
    imprint(reps);
    listeners.forEach(listener -> listener.occupancyMap(this));
  }

  public void addListener(OccupancyMapListener occupancyMapListener) {
    listeners.add(occupancyMapListener);
  }

  private static final Scalar FO2 = DoubleScalar.of(WIDTH / 2);

  /** @param points with dimensions == [n, 3] */
  private void imprint(Tensor points) {
    for (Tensor point : points) {
      int x = FO2.add(point.Get(0)).number().intValue();
      if (0 <= x && x < WIDTH) {
        int y = FO2.subtract(point.Get(1)).number().intValue();
        if (0 <= y && y < WIDTH) {
          graphics.setColor(new Color(128, 128, 128, 255));
          graphics.fillRect(x - 1, y - 1, 3, 3);
          // bufferedImage.setRGB(x, y, 255);
          bytes[x + WIDTH * y] = (byte) 255;
        }
      }
    }
  }

  /** @param points with dimensions == [n, 3] */
  private int evaluate(Tensor points) {
    int sum = 0;
    for (Tensor point : points) {
      int x = FO2.add(point.Get(0)).number().intValue();
      if (0 <= x && x < WIDTH) {
        int y = FO2.subtract(point.Get(1)).number().intValue();
        if (0 <= y && y < WIDTH)
          sum += bytes[x + WIDTH * y] & 0xff;
      }
    }
    return sum;
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public Tensor getPose() {
    return global.dot(pose);
  }
}
