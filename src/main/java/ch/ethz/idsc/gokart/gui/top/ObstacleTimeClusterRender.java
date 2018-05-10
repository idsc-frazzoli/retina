// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.gui.Colors;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.red.Mean;

/** used in {@link PresenterLcmModule} */
class ObstacleTimeClusterRender extends LidarRender implements ActionListener {
  final JToggleButton jToggleButton = new JToggleButton("cluster");
  // ---
  private boolean isClustering = true;
  private Tensor p1 = Tensors.empty();
  private Tensor pi = null;
  private Tensor data = Tensors.empty();
  // private Tensor hulls = Tensors.empty();
  // private Tensor mean = Tensors.empty();
  int i = 0;// TODO presently
  /** LidarRayBlockListener to be subscribed after LidarRender */
  LidarRayBlockListener lidarRayBlockListener = new LidarRayBlockListener() {
    @Override
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
      if (!isClustering)
        return;
      // ---
      Tensor points = _points;
      UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
      Tensor state = gokartPoseInterface.getPose(); // units {x[m], y[m], angle[]}
      unknownObstaclePredicate.setPose(state);
      Tensor p = Tensor.of(points.stream() //
          .filter(unknownObstaclePredicate::isObstacle) //
          .map(point -> point.extract(0, 2))); // only x,y matter
      i++;
      if (!Tensors.isEmpty(p)) {
        p1.append(p);
        if (p1.length() > 3) {
          p1 = Tensors.of(p1.get(p1.length() - 4), p1.get(p1.length() - 3), p1.get(p1.length() - 2), p1.get(p1.length() - 1));
          pi = ClusterConfig.GLOBAL.elkiDBSCANTime(Tensor.of(p1.flatten(1)));
        }
        {
          if (Objects.nonNull(pi)) {
            Tensor meanFour = Tensors.empty();
            Tensor _pi = pi;
            for (Tensor x : _pi) {
              if (!Tensors.isEmpty(x)) {
                Tensor mean = Tensors.empty();
                for (Tensor y : x) {
                  if (!Tensors.isEmpty(y)) {
                    mean.append(Mean.of(y));
                  }
                }
                meanFour.append(mean);
              }
            }
            data.append(meanFour);
            if (data.length() > 2)
              data = Tensors.of(data.get(data.length() - 2), data.get(data.length() - 1));
            System.out.println(data);
          }
        }
      }
    }
  };

  public ObstacleTimeClusterRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    jToggleButton.setSelected(isClustering);
    jToggleButton.addActionListener(this);
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isClustering)
      return;
    // ---
    Tensor mean = Tensors.empty();
    Tensor hulls = Tensors.empty();
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    if (Objects.nonNull(pi)) {
      Tensor _pi = pi;
      ColorDataIndexed colorDataIndexed = ColorDataLists._097;
      final int size = colorDataIndexed.size();
      {
        int i = 0;
        for (Tensor x : _pi) {
          graphics.setColor(colorDataIndexed.getColor(i % size));
          for (Tensor y : x) {
            if (!Tensors.isEmpty(y)) {
              mean.append(Mean.of(y));
            }
            hulls.append(ConvexHull.of(y));
            for (Tensor z : y) {
              Point2D point2D = geometricLayer.toPoint2D(z);
              graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
            }
          }
          ++i;
        }
      }
      {
        graphics.setColor(new Color(255, 0, 0, 255));
        for (Tensor w : mean) {
          Point2D point2D = geometricLayer.toPoint2D(w);
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 5, 5);
        }
      }
      {
        int i = 0;
        for (Tensor hull : hulls) {
          Color color = Colors.withAlpha(colorDataIndexed.getColor(i % size), 64);
          graphics.setColor(color);
          graphics.fill(geometricLayer.toPath2D(hull));
          ++i;
        }
      }
    }
    geometricLayer.popMatrix();
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    isClustering = jToggleButton.isSelected();
  }
}
