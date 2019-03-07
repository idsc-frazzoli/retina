//code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.UserName;

public class LidarClustering implements LidarRayBlockListener {
  private static final boolean ENABLED = UserName.is("valentinacavinato");
  // ---
  private final PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMapObstacles();
  private final SpacialXZObstaclePredicate nonFloorPredicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final UnknownObstacleGlobalPredicate unknownObstacleGlobalPredicate = //
      new UnknownObstacleGlobalPredicate(predefinedMap);
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart().unmodifiable();
  public final ClusterCollection collection;
  public boolean isClustering = ENABLED;
  private final GokartPoseInterface gokartPoseInterface;
  private final ClusterConfig clusterConfig;

  public LidarClustering(ClusterConfig clusterConfig, ClusterCollection collection, GokartPoseInterface gokartPoseInterface) {
    this.clusterConfig = clusterConfig;
    this.gokartPoseInterface = gokartPoseInterface;
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
    Tensor state = gokartPoseInterface.getPose(); // state if of the form {x[m], y[m], angle[]}
    GeometricLayer geometricLayer = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(state));
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
}
