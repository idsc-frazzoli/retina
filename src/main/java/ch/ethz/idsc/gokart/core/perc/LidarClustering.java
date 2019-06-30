//code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.pure.TrajectoryConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.app.cluster.ClusterCollection;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.UserName;

public class LidarClustering implements LidarRayBlockListener, GokartPoseListener {
  private static final boolean ENABLED = UserName.is("valentinacavinato");
  // ---
  private final PredefinedMap predefinedMap = TrajectoryConfig.getPredefinedMapObstacles();
  private final SpacialXZObstaclePredicate nonFloorPredicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final UnknownObstacleGlobalPredicate unknownObstacleGlobalPredicate = //
      new UnknownObstacleGlobalPredicate(predefinedMap);
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart().unmodifiable();
  public final ClusterCollection collection;
  private final ClusterConfig clusterConfig;
  // ---
  public boolean isClustering = ENABLED;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  public LidarClustering(ClusterConfig clusterConfig, ClusterCollection collection) {
    this.clusterConfig = clusterConfig;
    this.collection = collection;
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (!isClustering)
      return;
    // ---
    final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    final int position = floatBuffer.position();
    Tensor points = Tensors.empty();
    Tensor state = gokartPoseEvent.getPose(); // state if of the form {x[m], y[m], angle[]}
    GeometricLayer geometricLayer = GeometricLayer.of(PoseHelper.toSE2Matrix(state));
    geometricLayer.pushMatrix(lidar);
    while (floatBuffer.hasRemaining()) {
      float x = floatBuffer.get();
      float y = floatBuffer.get();
      float z = floatBuffer.get();
      if (nonFloorPredicate.isObstacle(x, z)) { // filter based on height
        Tensor global = geometricLayer.toVector(x, y); // z is dropped
        if (unknownObstacleGlobalPredicate.isObstacle(global))
          points.append(global);
      }
    }
    floatBuffer.position(position);
    if (Tensors.nonEmpty(points))
      synchronized (collection) {
        anteScan();
        double noiseRatio = clusterConfig.dbscanTracking(collection, points);
        postScan(points, noiseRatio);
      }
  }

  public void anteScan() {
    // override if necessary
  }

  public void postScan(Tensor points, double noiseRatio) {
    // override if necessary
  }

  @Override
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
