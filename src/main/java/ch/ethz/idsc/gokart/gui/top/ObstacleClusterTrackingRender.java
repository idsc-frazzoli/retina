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
import ch.ethz.idsc.tensor.opt.ConvexHull;

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
      Tensor state = gokartPoseInterface.getPose(); // units {x[m], y[m], angle[]}
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
      ColorDataIndexed colorDataIndexed = ColorDataLists._097;
      final int size = colorDataIndexed.size();
      {
        // int i = 0;
        for (ClusterDeque x : collection.collection) {
          graphics.setColor(colorDataIndexed.getColor(x.getID() % size));
          Tensor hulls = Tensors.empty();
          for (Tensor y : x.getDeque()) {
            hulls.append(ConvexHull.of(y));
            for (Tensor z : y) {
              Point2D point2D = geometricLayer.toPoint2D(z);
              graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
            }
          }
          {
            for (Tensor hull : hulls) {
              // Color color = Colors.withAlpha(colorDataIndexed.getColor(clusterColor % size), 64);
              // graphics.setColor(color);
              graphics.fill(geometricLayer.toPath2D(hull));
            }
          }
          {
            graphics.setColor(new Color(255, 0, 0, 128));
            Tensor nonEmptyMeans = x.getNonEmptyMeans();
            Path2D path2d = geometricLayer.toPath2D(nonEmptyMeans);
            graphics.draw(path2d);
          }
          // ++i;
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
