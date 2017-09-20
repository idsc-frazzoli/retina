// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.app.UniformResample;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class OccupancyMap implements LidarRayBlockListener {
  public static final int WIDTH = 1024;
  public static final float METER_TO_PIXEL = 50;
  public static final int LEVELS = 4;
  public static final Scalar M2PIX = RealScalar.of(METER_TO_PIXEL);
  // ---
  public Scalar threshold = RealScalar.of(40);
  public Scalar ds_value = RealScalar.of(0.03);
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_BYTE_GRAY);
  private final Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
  private final byte[] bytes;
  private Tensor global;
  private Tensor pose;
  private boolean optimize = false;
  private final Se2MultiresSamples se2MultiresSamples;
  private final List<OccupancyMapListener> listeners = new LinkedList<>();

  public OccupancyMap() {
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
    global = IdentityMatrix.of(3);
    pose = IdentityMatrix.of(3);
    se2MultiresSamples = new Se2MultiresSamples( //
        RealScalar.of(2 * Math.PI / 180), // 2 [deg]
        RealScalar.of(0.03 * METER_TO_PIXEL), // 3 [cm]
        LEVELS);
  }

  private int index = 0;

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    // System.out.println("enter "+index);
    ++index;
    GlobalAssert.that(lidarRayBlockEvent.dimensions == 2);
    Tensor points = Tensors.vector(i -> Tensors.vector( //
        lidarRayBlockEvent.floatBuffer.get(), //
        lidarRayBlockEvent.floatBuffer.get()), lidarRayBlockEvent.size());
    UniformResample uniformResample = new UniformResample(threshold, ds_value);
    final List<Tensor> total = uniformResample.apply(points);
    List<Tensor> mark = new LinkedList<>();
    for (Tensor block : total) {
      block = block.multiply(M2PIX);
      block.stream().forEach(row -> row.append(RealScalar.ONE));
      mark.add(block);
    }
    points = Tensor.of(mark.stream().flatMap(Tensor::stream));
    System.out.println(lidarRayBlockEvent.size() + " -> " + Dimensions.of(points));
    if (optimize) {
      // Stopwatch stp = Stopwatch.started();
      Scalar ang = RealScalar.of(2 * Math.PI / 180);
      Scalar shf = RealScalar.of(0.03 * METER_TO_PIXEL);
      for (int level = 0; level < 4; ++level) {
        // ---
        Tensor next = null;
        int cmp = 0;
        for (Tensor tryme : se2MultiresSamples.level(level)) {
          Tensor test = pose.dot(tryme);
          Tensor evl = Tensor.of(points.stream().map(row -> test.dot(row)));
          int ret = evaluate(evl);
          if (cmp < ret) {
            next = test;
            cmp = ret;
          }
        }
        pose = next;
        // ---
        ang = ang.multiply(RealScalar.of(.6));
        shf = shf.multiply(RealScalar.of(.6));
      }
    }
    optimize |= true;
    List<Tensor> reps = mark.stream() //
        .map(block -> Tensor.of(block.stream().map(row -> pose.dot(row)))) //
        .collect(Collectors.toList());
    imprint(reps);
    listeners.forEach(listener -> listener.occupancyMap(this));
    // System.out.println("exit "+index);
  }

  public void addListener(OccupancyMapListener occupancyMapListener) {
    listeners.add(occupancyMapListener);
  }

  private static final Scalar FO2 = DoubleScalar.of(WIDTH / 2);

  public Point2D toPoint2D(Tensor point) {
    return new Point2D.Double( //
        FO2.add(point.Get(0)).number().intValue(), //
        FO2.subtract(point.Get(1)).number().intValue());
  }

  /** @param points
   * with dimensions == [n, 3] */
  private void imprint(List<Tensor> total) {
    graphics.setColor(new Color(20, 20, 20, 255));
    for (Tensor points : total) {
      Path2D path2d = new Path2D.Double();
      Point2D p0 = toPoint2D(pose.get(Tensor.ALL, 2));
      path2d.moveTo(p0.getX(), p0.getY());
      for (int index = 0; index < points.length(); ++index) {
        Point2D p1 = toPoint2D(points.get(index));
        path2d.lineTo(p1.getX(), p1.getY());
      }
      graphics.fill(path2d);
    }
    // ---
    graphics.setColor(new Color(128, 128, 128, 255));
    for (Tensor points : total)
      for (Tensor point : points) {
        int x = FO2.add(point.Get(0)).number().intValue();
        if (0 <= x && x < WIDTH) {
          int y = FO2.subtract(point.Get(1)).number().intValue();
          if (0 <= y && y < WIDTH) {
            graphics.fillRect(x - 1, y - 1, 3, 3);
          }
        }
      }
    // ---
    graphics.setColor(Color.WHITE);
    for (Tensor points : total) {
      Path2D path2d = new Path2D.Double();
      Point2D p0 = toPoint2D(points.get(0));
      path2d.moveTo(p0.getX(), p0.getY());
      for (int index = 1; index < points.length(); ++index) {
        Point2D p1 = toPoint2D(points.get(index));
        path2d.lineTo(p1.getX(), p1.getY());
      }
      graphics.draw(path2d);
    }
  }

  /** @param points
   * with dimensions == [n, 3] */
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
