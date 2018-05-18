// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterDeque;
import ch.ethz.idsc.gokart.core.perc.DequeCloud;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** used in {@link PresenterLcmModule} */
class ObstacleClusterTrackingRender extends LidarRender implements ActionListener {
  private static final boolean ENABLED = UserHome.file("").getName().equals("valentinacavinato");
  final JToggleButton jToggleButton = new JToggleButton("cluster");
  // ---
  private ClusterCollection collection = new ClusterCollection();
  private boolean isClustering = ENABLED;
  /** LidarRayBlockListener to be subscribed after LidarRender */
  LidarRayBlockListener lidarRayBlockListener = new LidarRayBlockListener() {
    @Override
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
      if (!isClustering)
        return;
      // ---
      Tensor points = _points;
      UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
      Tensor state = gokartPoseInterface.getPose(); // units
                                                    // {x[m],
                                                    // y[m],
                                                    // angle[]}
      unknownObstaclePredicate.setPose(state);
      Tensor newScan = Tensor.of(points.stream() //
          .filter(unknownObstaclePredicate::isObstacle) //
          .map(point -> point.extract(0, 2))); // only x,y matter
      if (!Tensors.isEmpty(newScan)) {
        synchronized (collection) {
          ClusterConfig.GLOBAL.elkiDBSCANTracking(collection, newScan);
        }
      }
    }
  };

  public ObstacleClusterTrackingRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    jToggleButton.setSelected(isClustering);
    jToggleButton.addActionListener(this);
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isClustering)
      return;
    // ---
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    synchronized (collection) {
      // ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic().deriveWithAlpha(64);
      ColorDataIndexed colorDataPoints = ColorDataLists._250.cyclic().deriveWithAlpha(64);
      {
        for (ClusterDeque x : collection.getCollection()) {
          graphics.setColor(colorDataPoints.getColor(x.getID()));
          for (DequeCloud y : x.getDeque()) {
            if (Tensors.nonEmpty(y.hull())) {
              Path2D path2d = geometricLayer.toPath2D(y.hull());
              path2d.closePath();
              graphics.draw(path2d);
            }
          }
          for (DequeCloud y : x.getDeque()) {
            for (Tensor z : y.points()) {
              Point2D point2D = geometricLayer.toPoint2D(z);
              graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
            }
          }
          {
            graphics.setColor(new Color(255, 0, 0, 128));
            Tensor nonEmptyMeans = x.getNonEmptyMeans();
            Path2D path2d = geometricLayer.toPath2D(nonEmptyMeans);
            graphics.draw(path2d);
          }
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
