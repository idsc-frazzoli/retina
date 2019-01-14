// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pure.TrajectoryConfig;
import ch.ethz.idsc.gokart.core.pure.TrajectoryLcmClient;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Entrywise;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.InvertUnlessZero;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ abstract class TrajectoryVideo implements OfflineLogListener {
  private static final TensorUnaryOperator GEODESIC_CENTER_FILTER = //
      GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), 5);
  private static final Scalar METER2PIXEL = RealScalar.of(30);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  final Tensor waypoints = Nest.of(new BSpline1CurveSubdivision(Se2Geodesic.INSTANCE)::cyclic, TrajectoryConfig.getWaypoints(), 1);
  private static final Tensor ARROW_HEAD = Arrowhead.of(.4);
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  // ---
  private int count = 0;
  private Tensor trail = Tensors.empty();
  private Tensor times = Tensors.empty();
  private List<TrajectorySample> trajectory;

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(byteBuffer);
      Tensor pose = GokartPoseHelper.toUnitless(gokartPoseEvent.getPose());
      trail.append(pose);
      times.append(time);
    } else //
    if (channel.equals(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME)) {
      // TODO JPH render image
      if (Tensors.nonEmpty(trail) && Objects.nonNull(trajectory) && !trajectory.isEmpty()) {
        Tensor filtered = GEODESIC_CENTER_FILTER.apply(trail);
        Tensor planned = Tensor.of(trajectory.stream().map(TrajectorySample::stateTime).map(StateTime::state));
        // ---
        Tensor speeds = LIE_DIFFERENCES.apply(filtered);
        Tensor dt = Differences.of(times).map(Magnitude.SECOND).map(InvertUnlessZero.FUNCTION);
        final Scalar mean = Mean.of(speeds.get(Tensor.ALL, 0).pmul(dt)).Get();
        // TODO JPH make more elegant
        Stream<Tensor> a = filtered.stream().map(Extract2D.FUNCTION);
        Stream<Tensor> b = planned.stream().map(Extract2D.FUNCTION);
        Tensor reduceMin = Stream.concat(a, b).reduce(Entrywise.min()).get();
        // System.out.println(reduceMin);
        reduceMin = Tensors.vector(30, 34);
        a = filtered.stream().map(Extract2D.FUNCTION);
        b = planned.stream().map(Extract2D.FUNCTION);
        Tensor reduceMax = Stream.concat(a, b).reduce(Entrywise.max()).get();
        reduceMax = Tensors.vector(60, 62);
        Tensor extensions = reduceMax.subtract(reduceMin);
        Tensor dimensions = extensions.multiply(METER2PIXEL);
        BufferedImage bufferedImage = new BufferedImage( //
            dimensions.Get(0).number().intValue(), //
            dimensions.Get(1).number().intValue(), //
            BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        Tensor tr = Se2Utils.toSE2Translation(reduceMin.negate());
        // System.out.println(extensions);
        Tensor sc = Tensors.fromString("{{30,0,1},{0,-30," + (bufferedImage.getHeight() - 1) + "},{0,0,1}}");
        GeometricLayer geometricLayer = GeometricLayer.of(sc.dot(tr));
        reduceMin.append(RealScalar.ZERO);
        GraphicsUtil.setQualityHigh(graphics);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
        for (Tensor waypoint : waypoints) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(waypoint));
          Path2D path2d = geometricLayer.toPath2D(ARROW_HEAD);
          path2d.closePath();
          graphics.fill(path2d);
          geometricLayer.popMatrix();
        }
        graphics.setStroke(new BasicStroke(2.5f));
        graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
        graphics.draw(geometricLayer.toPath2D(planned));
        graphics.setStroke(new BasicStroke(3.5f));
        graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
        graphics.draw(geometricLayer.toPath2D(filtered));
        graphics.setColor(Color.DARK_GRAY);
        graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 22));
        graphics.drawString("time: " + time.map(Round._1), 0, 22);
        graphics.drawString("avg speed: " + mean.map(Round._1) + "[m/s]", 0, 22 + 22);
        ++count;
        if (count < 10000) {
          System.out.println(count);
          image(bufferedImage);
        }
        // File file = UserHome.Pictures(String.format("trajectory/ex%06d.png", count++));
        // System.out.println(++count);
        // try {
        // ImageIO.write(bufferedImage, "png", file);
        // } catch (Exception exception) {
        // exception.printStackTrace();
        // }
      }
      // ---
      trail = Tensors.empty();
      times = Tensors.empty();
      trajectory = TrajectoryLcmClient.trajectory(byteBuffer);
    }
  }

  public abstract void image(BufferedImage bufferedImage);

  public static void main(String[] args) throws Exception {
    File file = DatahakiLogFileLocator.INSTANCE.getAbsoluteFile(GokartLogFile._20180904T183437_b00c893a);
    System.out.println(file);
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(HomeDirectory.file("video.mp4").getPath(), new Dimension(900, 840), 2)) {
      TrajectoryVideo trajectoryImages = new TrajectoryVideo() {
        @Override
        public void image(BufferedImage bufferedImage) {
          mp4.append(bufferedImage);
        }
      };
      OfflineLogPlayer.process(file, trajectoryImages);
    }
  }
}
