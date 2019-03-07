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

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ResampledLidarRender extends LidarRender {
  private boolean flagMapCreate = false;
  private boolean flagMapUpdate = false;
  public final UpdatedMap updatedMap = new UpdatedMap();

  public ResampledLidarRender(MappedPoseInterface mappedPoseInterface) {
    super(mappedPoseInterface);
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(_points))
      return;
    final Tensor points = _points;
    // System.out.println("IN=" + supplier.get());
    final Tensor lidar = Se2Utils.toSE2Matrix(supplier.get());
    geometricLayer.pushMatrix(lidar);
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    final List<Tensor> list = LocalizationConfig.GLOBAL.getResample().apply(points).getPoints();
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
      updatedMap.intake(geometricLayer.getMatrix(), list);
      updatedMap.store();
    }
    geometricLayer.popMatrix();
  }

  public final ActionListener action_mapCreate = e -> flagMapCreate = true;
  public final ActionListener action_mapUpdate = e -> flagMapUpdate = true;
  public final ActionListener action_snap = e -> LidarLocalizationModule.FLAGSNAP = true;
}
