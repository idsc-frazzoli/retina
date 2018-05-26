// code by vc
package ch.ethz.idsc.demo.vc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterDeque;
import ch.ethz.idsc.gokart.core.perc.SimplePredictor;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class Handler {
  UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
  private ClusterCollection collection = new ClusterCollection();
  /** LidarRayBlockListener to be subscribed after LidarRender */
  LidarRayBlockListener lidarRayBlockListener = new LidarRayBlockListener() {
    @Override
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
      System.out.println("we have clusters" + collection.getCollection().size());
      final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      final int position = floatBuffer.position();
      Tensor points = Tensors.empty();
      while (floatBuffer.hasRemaining()) {
        double x = floatBuffer.get();
        double y = floatBuffer.get();
        double z = floatBuffer.get();
        // no filter based on height
        points.append(Tensors.vectorDouble(x, y, z));
      }
      floatBuffer.position(position);
      // ---
      Tensor newScan = Tensor.of(points.stream() //
          .filter(unknownObstaclePredicate::isObstacle) //
          .map(point -> point.extract(0, 2))); // only x,y matter
      if (Tensors.nonEmpty(newScan)) {
        synchronized (collection) {
          ClusterConfig.GLOBAL.elkiDBSCANTracking(collection, newScan);
          Tensor predictedHulls = Tensors.empty();
          Tensor predictedMeans = Tensors.empty();
          for (ClusterDeque x : collection.getCollection()) {
            if (Tensors.nonEmpty(x.getNonEmptyMeans())) {
              Tensor predictedMean = SimplePredictor.getMeanPrediction(x);
              Tensor predictedHull = SimplePredictor.getHullPrediction(x);
              predictedMeans.append(predictedMean);
              predictedHulls.append(predictedHull);
            }
          }
          if (0 < predictedMeans.length()) {
            double evaluatePerformance = evaluatePerformance(predictedMeans, predictedHulls);
            System.out.println("performance=" + evaluatePerformance);
          }
          PerformanceMeasures measures = computeRecall(predictedHulls, newScan);
          System.out.println("recall=" + measures.recall + '\n' + "precision=" + measures.precision);
        }
      } else
        System.err.println("scan is empty");
    }
  };
  private double side = 0.03;

  // basic performance measure: compute the fraction of predicted centres of clusters that are
  // in the convexHull of the new lidar scan clusters
  public double evaluatePerformance(Tensor predictedMeans, Tensor hulls) {
    int count = 0;
    for (Tensor z : predictedMeans) {
      for (Tensor hull : hulls) {
        int i = Polygons.isInside(hull, z) ? 1 : 0;
        count += i;
      }
    }
    return count / (double) predictedMeans.length();
  }

  public PerformanceMeasures computeRecall(Tensor predictedShapes, Tensor newScan) {
    Tensor results = Tensors.empty();
    Enlarger enlargedPoints = new Enlarger(newScan, side);
    System.out.println("Area of points" + enlargedPoints.getTotalArea());
    Enlarger predictedAreas = new Enlarger(predictedShapes);
    System.out.println("Area of hulls" + predictedAreas.getTotalArea());
    for (Tensor x : predictedAreas.getAreas()) {
      for (Tensor y : enlargedPoints.getAreas()) {
        if (Tensors.nonEmpty(PolygonIntersector.polygonIntersect(x, y))) {
          results.append(PolygonIntersector.polygonIntersect(x, y));
        }
      }
    }
    Enlarger res = new Enlarger(results);
    double area = res.getTotalArea();
    System.out.println("Area of intersection" + area);
    return new PerformanceMeasures( //
        area / enlargedPoints.getTotalArea(), //
        area / predictedAreas.getTotalArea());
  }
}

// TODO rename class
enum GokartRayLogExport {
  ;
  public static void main(String[] args) throws IOException {
    final String channel = VelodyneLcmChannels.ray(VelodyneModel.VLP16, "center");
    Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    Handler handler = new Handler();
    vlp16LcmHandler.lidarAngularFiringCollector.addListener(handler.lidarRayBlockListener);
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String _channel, ByteBuffer byteBuffer) {
        if (_channel.equals(channel))
          vlp16LcmHandler.velodyneDecoder.lasers(byteBuffer);
        else if (_channel.equals(GokartLcmChannel.POSE_LIDAR)) {
          GokartPoseEvent gpe = new GokartPoseEvent(byteBuffer);
          handler.unknownObstaclePredicate.setPose(gpe.getPose());
        }
      }
    };
    File file = UserHome.file("/Desktop/ETHZ/log/pedestrian/20180412T163855/log.lcm");
    OfflineLogPlayer.process(file, offlineLogListener);
  }
}
