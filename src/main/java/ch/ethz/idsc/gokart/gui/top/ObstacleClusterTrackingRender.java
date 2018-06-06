// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;

import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterDeque;
import ch.ethz.idsc.gokart.core.perc.DequeCloud;
import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.UnknownObstacleGlobalPredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

/** used in {@link PresenterLcmModule} */
class ObstacleClusterTrackingRender implements LidarRayBlockListener, RenderInterface, ActionListener {
  private static final boolean ENABLED = UserHome.file("").getName().equals("valentinacavinato");
  private static final Color COLOR_TRACE = new Color(255, 0, 0, 128);
  // ---
  private final PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMapObstacles();
  private final UnknownObstacleGlobalPredicate unknownObstacleGlobalPredicate = //
      new UnknownObstacleGlobalPredicate(predefinedMap);
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart().unmodifiable();
  // ---
  final JToggleButton jToggleButton = new JToggleButton("cluster");
  // ---
  private ClusterCollection collection = new ClusterCollection();
  private boolean isClustering = ENABLED;
  private final ColorDataIndexed colorDataIndexed = ColorDataLists._250.cyclic().deriveWithAlpha(64);
  private final GokartPoseInterface gokartPoseInterface;

  public ObstacleClusterTrackingRender(GokartPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
    jToggleButton.setSelected(isClustering);
    jToggleButton.addActionListener(this);
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (!isClustering)
      return;
    // ---
    final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    final int position = floatBuffer.position();
    Tensor points = Tensors.empty();
    SpacialXZObstaclePredicate nonFloorPredicate = SimpleSpacialObstaclePredicate.createVlp16();
    Tensor state = gokartPoseInterface.getPose(); // state if of the form {x[m], y[m], angle[]}
    GeometricLayer geometricLayer = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(state));
    geometricLayer.pushMatrix(lidar);
    while (floatBuffer.hasRemaining()) {
      double x = floatBuffer.get();
      double y = floatBuffer.get();
      double z = floatBuffer.get();
      if (nonFloorPredicate.isObstacle(x, z)) { // filter based on height
        Tensor local = Tensors.vectorDouble(x, y); // z is dropped
        Point2D pnt = geometricLayer.toPoint2D(local);
        Tensor global = Tensors.vectorDouble(pnt.getX(), pnt.getY());
        if (unknownObstacleGlobalPredicate.isObstacle(global))
          points.append(global);
      }
    }
    floatBuffer.position(position);
    if (Tensors.nonEmpty(points))
      synchronized (collection) {
        ClusterConfig.GLOBAL.dbscanTracking(collection, points);
      }
  }

  @Override // from AbstractGokartRender
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isClustering)
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
    isClustering = jToggleButton.isSelected();
  }
}
