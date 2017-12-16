// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.Degree;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.alg.slam.Se2MultiresSamples;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.util.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.sca.Round;

// TODO this is not the final API:
// the points should be resampled after each scan and not before each draw!
class ResampledLidarRender extends LidarRender {
  private boolean flagStoreMap = false;
  private boolean flagSnap = false;
  private final Se2MultiresSamples se2MultiresSamples = //
      new Se2MultiresSamples(RealScalar.of(1), Degree.of(1), 3, 2);
  BufferedImage map_image = null;
  public final ActionListener action_storeMap = new ActionListener() {
    @Override // from ActionListener
    public void actionPerformed(ActionEvent e) {
      System.out.println("request to store map");
      flagStoreMap = true;
    }
  };
  public final ActionListener action_snap = new ActionListener() {
    @Override // from ActionListener
    public void actionPerformed(ActionEvent e) {
      System.out.println("request to snap to map");
      flagSnap = true;
    }
  };

  public ResampledLidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    // ---
    try {
      Tensor tensor = Import.of(UserHome.Pictures("master_map.png"));
      map_image = ImageFormat.of(tensor);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(_points))
      return;
    final Tensor points = _points;
    final List<Tensor> list = LocalizationConfig.GLOBAL.getUniformResample().apply(points);
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    graphics.drawImage(map_image, 0, 0, map_image.getWidth(), map_image.getHeight(), null);
    if (flagSnap) {
      flagSnap = false;
      // TEST ONLY
      Tensor model2pixel = geometricLayer.getMatrix();
      GeometricLayer glmap = new GeometricLayer(model2pixel, Array.zeros(3));
      // glmap.toPoint2D(x);
      SlamDunk slamDunk = new SlamDunk(map_image);
      slamDunk.set(se2MultiresSamples);
      Tensor result = slamDunk.fit(glmap, list);
      System.out.println(Pretty.of(result.map(Round._4)));
      // Graphics2D mapGfx = (Graphics2D) map_image.getGraphics();
      // mapGfx.setColor(new Color(100, 100, 100));
      // for (Tensor pnts : list) {
      // for (Tensor x : pnts) {
      // Point2D point2D = glmap.toPoint2D(x);
      // mapGfx.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
      // }
      // }
      // System.out.println("inserted into map");
    }
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    {
      // ---
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
      if (flagStoreMap) {
        flagStoreMap = false;
        StoreMapUtil.createNew(geometricLayer, list);
      }
    }
    geometricLayer.popMatrix();
  }
}
