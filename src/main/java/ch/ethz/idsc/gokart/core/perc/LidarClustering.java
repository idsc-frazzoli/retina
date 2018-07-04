//code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LidarClustering implements LidarRayBlockListener {
  private static final boolean ENABLED = UserHome.file("").getName().equals("valentinacavinato");
  private final PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMapObstacles();
  private final SpacialXZObstaclePredicate nonFloorPredicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final UnknownObstacleGlobalPredicate unknownObstacleGlobalPredicate = //
      new UnknownObstacleGlobalPredicate(predefinedMap);
  private final Tensor lidar = SensorsConfig.GLOBAL.vlp16Gokart().unmodifiable();
  private ClusterCollection collection;
  public boolean isClustering = ENABLED;
  private final GokartPoseInterface gokartPoseInterface;

  public LidarClustering(ClusterCollection collection, GokartPoseInterface gokartPoseInterface) {
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
        double noiseRatio = ClusterConfig.GLOBAL.dbscanTracking(collection, points);
        postScan(points, noiseRatio);
      }
  }

  public void postScan(Tensor points, double noiseRatio) {
    // TODO Auto-generated method stub
  }

  public void anteScan() {
    // TODO Auto-generated method stub
  }
}
