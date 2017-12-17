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
import java.util.Objects;
import java.util.stream.Collectors;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.gui.TensorGraphics;
import ch.ethz.idsc.retina.util.math.UniformResample;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.LinearSolve;

public class OccupancyMap implements LidarRayBlockListener {
  public static final int WIDTH = 1200;
  private static final Scalar FO2 = DoubleScalar.of(WIDTH / 2);
  public static final int LEVELS = 4;
  private final Scalar M2PIX;
  private static final Color FREESPACE = new Color(20, 20, 20, 255);
  private static final Color NEXTSPACE = new Color(128, 128, 128, 255);
  private static final Color WALLSPACE = new Color(255, 255, 255, 255);
  // ---
  public Scalar threshold = RealScalar.of(100);
  public Scalar ds_value = RealScalar.of(0.05);
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_BYTE_GRAY);
  private final Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
  private final byte[] bytes;
  private Tensor global;
  private Tensor pose;
  private boolean hasPrev = false;
  private final Se2MultiresSamples se2MultiresSamples;
  private final List<SlamListener> listeners = new LinkedList<>();

  /** in the j2b2 project m2p == 50 */
  public OccupancyMap(float METER_TO_PIXEL, Se2MultiresSamples se2MultiresSamples) {
    M2PIX = RealScalar.of(METER_TO_PIXEL);
    // ---
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    bytes = dataBufferByte.getData();
    global = IdentityMatrix.of(3);
    pose = IdentityMatrix.of(3);
    this.se2MultiresSamples = se2MultiresSamples;
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    synchronized (pose) {
      GlobalAssert.that(lidarRayBlockEvent.dimensions == 2);
      Tensor points = Tensors.vector(i -> Tensors.vector( //
          lidarRayBlockEvent.floatBuffer.get(), //
          lidarRayBlockEvent.floatBuffer.get()), lidarRayBlockEvent.size());
      final UniformResample uniformResample = new UniformResample(threshold, ds_value);
      final List<Tensor> total = uniformResample.apply(points);
      // System.out.println(total.stream().map(Tensor::length).collect(Collectors.toList()));
      // Tensor l = Tensor.of(total.stream().map(Tensor::length).map(RealScalar::of));
      // System.out.println(Tally.sorted(l));
      final List<Tensor> mark = new LinkedList<>();
      for (Tensor block : total) {
        block = block.multiply(M2PIX);
        block.stream().forEach(row -> row.append(RealScalar.ONE));
        mark.add(block);
      }
      points = Tensor.of(mark.stream().flatMap(Tensor::stream));
      // System.out.println(lidarRayBlockEvent.size() + " -> " + Dimensions.of(points));
      Tensor prev = pose.copy();
      if (hasPrev) {
        // Stopwatch stp = Stopwatch.started();
        for (int level = 0; level < LEVELS; ++level) {
          Tensor next = null;
          int cmp = -1;
          for (Tensor tryme : se2MultiresSamples.level(level)) {
            GlobalAssert.that(Objects.nonNull(pose));
            GlobalAssert.that(Objects.nonNull(tryme));
            Tensor test = pose.dot(tryme);
            Tensor evl = Tensor.of(points.stream().map(row -> test.dot(row)));
            int ret = evaluate(evl);
            GlobalAssert.that(0 <= ret);
            if (cmp < ret) {
              next = test;
              cmp = ret;
            }
          }
          if (cmp == 0)
            System.err.println("cmp==0");
          pose = next;
        }
      }
      hasPrev |= true;
      final List<Tensor> pose_lidar = mark.stream() //
          .map(block -> Tensor.of(block.stream().map(row -> pose.dot(row)))) //
          .collect(Collectors.toList());
      imprintFreeSpace(pose_lidar);
      imprintNextSpace(pose_lidar);
      imprintWallSpace(pose_lidar);
      SlamEvent slamEvent = new SlamEvent();
      slamEvent.global_pose = global.dot(pose);
      slamEvent.move = LinearSolve.of(prev, pose);
      slamEvent.bufferedImage = bufferedImage;
      slamEvent.pose_lidar = mark; // pose_lidar;
      listeners.forEach(listener -> listener.slam(slamEvent));
    }
  }

  public void addListener(SlamListener occupancyMapListener) {
    listeners.add(occupancyMapListener);
  }

  public static Point2D toPoint2D(Tensor point) {
    return new Point2D.Double( //
        FO2.add(point.Get(0)).number().intValue(), //
        FO2.subtract(point.Get(1)).number().intValue());
  }

  private void imprintFreeSpace(List<Tensor> total) {
    graphics.setColor(FREESPACE);
    for (Tensor points : total) {
      Path2D path2D = new Path2D.Double();
      {
        Point2D point2D = toPoint2D(pose.get(Tensor.ALL, 2));
        path2D.moveTo(point2D.getX(), point2D.getY());
      }
      for (int index = 0; index < points.length(); ++index) {
        Point2D point2D = toPoint2D(points.get(index));
        path2D.lineTo(point2D.getX(), point2D.getY());
      }
      graphics.fill(path2D);
    }
  }

  /** @param points with dimensions == [n, 3] */
  private void imprintNextSpace(List<Tensor> total) {
    graphics.setColor(NEXTSPACE);
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
  }

  private void imprintWallSpace(List<Tensor> total) {
    graphics.setColor(WALLSPACE);
    for (Tensor points : total)
      graphics.draw(TensorGraphics.polygonToPath(points, OccupancyMap::toPoint2D));
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
}
