// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.alg.slam.Se2MultiresSamples;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.util.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

// TODO this is not the final API:
// the points should be resampled after each scan and not before each draw!
class ResampledLidarRender extends LidarRender {
  private final GokartPoseInterface gokartPoseInterface;
  private boolean flagMapCreate = false;
  private boolean flagMapUpdate = false;
  private boolean flagSnap = false;
  private boolean flagSetLocation = false;
  private final Se2MultiresSamples se2MultiresSamples = //
      new Se2MultiresSamples(RealScalar.of(1), Degree.of(1), 9, 2); // TODO during operation, only 3-5 levels should be used
  private BufferedImage map_image = null;

  public ResampledLidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    // ---
    this.gokartPoseInterface = gokartPoseInterface;
    map_image = StoreMapUtil.loadOrNull();
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(_points))
      return;
    { // model2pixel 7.5 means 1[m] translates to 7.5 pixel, 13.3[cm] per pixel
      Tensor mat = geometricLayer.getMatrix();
      Scalar det = Det.of(mat);
      // System.out.println("factor=" + Sqrt.of(det.negate()));
    }
    final Tensor points = _points;
    final List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample().apply(points);
    final Tensor lidar = Se2Utils.toSE2Matrix(supplier.get());
    geometricLayer.pushMatrix(lidar);
    if (Objects.nonNull(map_image)) {
      graphics.drawImage(map_image, 0, 0, map_image.getWidth(), map_image.getHeight(), null);
      if (flagSnap) {
        flagSnap = false;
        // TEST ONLY
        Tensor model2pixel = geometricLayer.getMatrix();
        SlamDunk slamDunk = new SlamDunk(map_image);
        slamDunk.set(se2MultiresSamples);
        GeometricLayer glmap = new GeometricLayer(model2pixel, Array.zeros(3));
        Tensor delta = slamDunk.fit(glmap, list);
        System.out.println(Pretty.of(delta.map(Round._4)));
        Tensor poseDelta = lidar.dot(delta).dot(Inverse.of(lidar));
        poseDelta.set(s -> Quantity.of(s.Get(), "m"), 0, 2);
        poseDelta.set(s -> Quantity.of(s.Get(), "m"), 1, 2);
        System.out.println(Pretty.of(poseDelta.map(Round._4)));
        Tensor state = gokartPoseInterface.getPose(); // {x[m],y[y],angle[]}
        Tensor newPose = Se2Utils.toSE2Matrix(state).dot(poseDelta);
        Tensor newState = Se2Utils.fromSE2Matrix(newPose);
        System.out.println(newState);
        gokartPoseInterface.setPose(newState);
      }
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
      graphics.setColor(Color.BLACK);
      int total = list.stream().mapToInt(l -> l.length()).sum();
      graphics.drawString("resampled " + total, 0, 50);
    }
    if (flagMapCreate) {
      flagMapCreate = false;
      map_image = StoreMapUtil.createNew(geometricLayer, list);
    }
    if (flagMapUpdate) {
      flagMapUpdate = false;
      // StoreMapUtil.createNew(geometricLayer, list);
      StoreMapUtil.updateMap(geometricLayer, list, map_image);
    }
    geometricLayer.popMatrix();
  }

  public final ActionListener action_mapCreate = e -> flagMapCreate = true;
  public final ActionListener action_mapUpdate = e -> flagMapUpdate = true;
  public final ActionListener action_snap = e -> flagSnap = true;
  public final ActionListener action_setLocation = e -> flagSetLocation = true;
}
