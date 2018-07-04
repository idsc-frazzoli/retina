// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterDeque;
import ch.ethz.idsc.gokart.core.perc.DequeCloud;
import ch.ethz.idsc.gokart.core.perc.LidarClustering;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** used in {@link PresenterLcmModule} */
class ObstacleClusterTrackingRender implements RenderInterface, ActionListener {
  private static final Color COLOR_TRACE = new Color(255, 0, 0, 128);
  // ---
  // ---
  final JToggleButton jToggleButton = new JToggleButton("cluster");
  // ---
  private ClusterCollection collection = new ClusterCollection();
  private final ColorDataIndexed colorDataIndexed = ColorDataLists._250.cyclic().deriveWithAlpha(64);
  public final LidarClustering lidarClustering;

  public ObstacleClusterTrackingRender(GokartPoseInterface gokartPoseInterface) {
    lidarClustering = new LidarClustering(collection, gokartPoseInterface);
    jToggleButton.setSelected(lidarClustering.isClustering);
    jToggleButton.addActionListener(this);
  }

  @Override // from AbstractGokartRender
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!lidarClustering.isClustering)
      return;
    // ---
    synchronized (collection) {
      for (ClusterDeque clusterDeque : collection.getCollection()) {
        graphics.setColor(colorDataIndexed.getColor(clusterDeque.getID()));
        for (DequeCloud dequeCloud : clusterDeque.getDeque())
          if (Tensors.nonEmpty(dequeCloud.hull())) {
            Path2D path2d = geometricLayer.toPath2D(dequeCloud.hull());
            path2d.closePath();
            graphics.draw(path2d);
          }
        for (DequeCloud dequeCloud : clusterDeque.getDeque())
          for (Tensor point : dequeCloud.points()) {
            Point2D point2D = geometricLayer.toPoint2D(point);
            graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
          }
        {
          graphics.setColor(COLOR_TRACE);
          Tensor nonEmptyMeans = clusterDeque.getNonEmptyMeans();
          Path2D path2d = geometricLayer.toPath2D(nonEmptyMeans);
          graphics.draw(path2d);
        }
      }
    }
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    lidarClustering.isClustering = jToggleButton.isSelected();
  }
}
