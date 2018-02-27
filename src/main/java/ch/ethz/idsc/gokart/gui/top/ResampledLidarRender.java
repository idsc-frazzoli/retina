// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.slam.DubendorfSlam;
import ch.ethz.idsc.gokart.core.slam.LidarGyroLocalization;
import ch.ethz.idsc.gokart.core.slam.SlamDunk;
import ch.ethz.idsc.gokart.core.slam.SlamResult;
import ch.ethz.idsc.gokart.core.slam.SlamScore;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.gui.GraphicsUtil;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

// TODO this is not the final API:
// the points should be resampled after each scan and not before each draw!
// the localization should happen in a separate thread that does not require the window to be open
public class ResampledLidarRender extends LidarRender {
  private final MappedPoseInterface mappedPoseInterface;
  private boolean flagMapCreate = false;
  private boolean flagMapUpdate = false;
  private boolean flagSnap = false;
  private PredefinedMap predefinedMap = PredefinedMap.DUBENDORF_HANGAR_20180122;

  public ResampledLidarRender(MappedPoseInterface mappedPoseInterface) {
    super(mappedPoseInterface);
    // ---
    this.mappedPoseInterface = mappedPoseInterface;
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(_points))
      return;
    { // model2pixel 7.5 means 1[m] translates to 7.5 pixel, 13.3[cm] per pixel
      // Tensor mat = geometricLayer.getMatrix();
      // Scalar det = Det.of(mat);
      // System.out.println("factor=" + Sqrt.of(det.negate()));
    }
    final Tensor points = _points;
    final List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample().apply(points).getPoints();
    final Tensor lidar = Se2Utils.toSE2Matrix(supplier.get());
    geometricLayer.pushMatrix(lidar);
    // System.out.println(Pretty.of(geometricLayer.getMatrix().map(Round._5)));
    {
      graphics.drawImage(predefinedMap.getImage(), 0, 0, null);
      Tensor scattered = Tensor.of(list.stream().flatMap(Tensor::stream));
      int sum = scattered.length(); // usually around 430
      // System.out.println("points: " + sum);
      if (flagSnap || trackSupplier.get())
        if (LidarGyroLocalization.MIN_POINTS < sum) {
          flagSnap = false;
          // ---
          Tensor model2pixel = geometricLayer.getMatrix();
          SlamScore slamScore = ImageScore.of(predefinedMap.getImageExtruded());
          GeometricLayer glmap = new GeometricLayer(model2pixel, Array.zeros(3));
          Stopwatch stopwatch = Stopwatch.started();
          SlamResult slamResult = SlamDunk.of(DubendorfSlam.SE2MULTIRESGRIDS, glmap, scattered, slamScore);
          Tensor delta = slamResult.getTransform();
          double duration = stopwatch.display_seconds();
          // System.out.println(duration + "[s]");
          final Scalar ratio = slamResult.getMatchRatio();
          // System.out.println(Pretty.of(delta.map(Round._4)));
          Tensor poseDelta = lidar.dot(delta).dot(Inverse.of(lidar));
          poseDelta.set(s -> Quantity.of(s.Get(), SI.METER), 0, 2);
          poseDelta.set(s -> Quantity.of(s.Get(), SI.METER), 1, 2);
          // System.out.println(Pretty.of(poseDelta.map(Round._4)));
          Tensor state = mappedPoseInterface.getPose(); // {x[m],y[y],angle[]}
          Tensor newPose = Se2Utils.toSE2Matrix(state).dot(poseDelta);
          Tensor newState = Se2Utils.fromSE2Matrix(newPose);
          // System.out.println(newState);
          mappedPoseInterface.setPose(newState, ratio);
          // ---
          graphics.setColor(Color.GRAY);
          graphics.drawString("points=" + sum, 0, 30);
          graphics.drawString("quality=" + ratio.map(Round._2), 0, 50);
          graphics.drawString("duration=" + Quantity.of(duration, SI.SECOND).map(Round._4), 0, 70);
        } else
          System.err.println("insufficient: " + sum);
    }
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    {
      graphics.setColor(color);
      for (Tensor pnts : list) {
        for (Tensor x : pnts) {
          Point2D point2D = geometricLayer.toPoint2D(x);
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
        }
        Path2D path2D = geometricLayer.toPath2D(pnts);
        int col;
        col = 128;
        GraphicsUtil.setQualityHigh(graphics);
        graphics.setColor(new Color(col, col, col, 255));
        graphics.setStroke(new BasicStroke(3f));
        graphics.draw(path2D);
        col = 0;
        graphics.setColor(new Color(col, col, col, 255));
        graphics.setStroke(new BasicStroke(1f));
        graphics.draw(path2D);
        GraphicsUtil.setQualityDefault(graphics);
      }
    }
    if (flagMapCreate) {
      flagMapCreate = false;
      System.err.println("action not supported");
      // map_image = StoreMapUtil.createNew(geometricLayer, list);
    }
    if (flagMapUpdate) {
      flagMapUpdate = false;
      System.err.println("action not supported");
      // StoreMapUtil.updateMap(geometricLayer, list, map_image);
    }
    geometricLayer.popMatrix();
  }

  public final ActionListener action_mapCreate = e -> flagMapCreate = true;
  public final ActionListener action_mapUpdate = e -> flagMapUpdate = true;
  public final ActionListener action_snap = e -> flagSnap = true;
  public Supplier<Boolean> trackSupplier = () -> false;
}
