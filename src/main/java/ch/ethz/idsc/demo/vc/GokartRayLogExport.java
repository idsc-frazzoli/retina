// code by jph
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterDeque;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
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
import ch.ethz.idsc.tensor.opt.ConvexHull;

class Handler {
  UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
  private ClusterCollection collection = new ClusterCollection();
  PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
  GeometricLayer geometricLayer = new GeometricLayer(predefinedMap.getModel2Pixel(), Tensors.vector(0, 0, 0));
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
      if (!Tensors.isEmpty(newScan)) {
        synchronized (collection) {
          ClusterConfig.GLOBAL.elkiDBSCANTracking(collection, newScan);
          Tensor test = Tensors.empty();
          double evaluatePerformance = evaluatePerformance(test);
          System.out.println("recall=" + evaluatePerformance);
        }
      } else
        System.err.println("scan is empty");
      
    }
  };

  // basic performance measure: compute the fraction of predicted centres of clusters that are in the convexHull
  // of the new lidar scan clusters
  public double evaluatePerformance(Tensor predictedMeans) {
  
   
    int count = 0;
    Tensor hulls = Tensors.empty();
    for (ClusterDeque x : collection.getCollection()) {
      int k = x.getDeque().size();
      int i = 0;
      for (Tensor y : x.getDeque()) {
        if (k == (i + 1))
          hulls.append(ConvexHull.of(y));
        ++i;
      }
      Tensor nm = x.getNonEmptyMeans(); // just to test
      if (!Tensors.isEmpty(nm))
        predictedMeans.append(nm.get(nm.length() - 1));
    }
    for (Tensor z : predictedMeans) {
      for (Tensor hull : hulls) {
        int i = geometricLayer.toPath2D(hull).contains(z.Get(0).number().doubleValue(), z.Get(1).number().doubleValue()) ? 1 : 0;
        count = count + i;
      }
      System.out.println(count);
    }
    
   
    if (!Tensors.isEmpty(predictedMeans))
      return count / predictedMeans.length();
    return 0;
  }

  public double computeRecall(Tensor predictedMeans, Tensor newScan) {
    EnlargedPoints enlargedMeans = new EnlargedPoints(predictedMeans);
    EnlargedPoints enlargedPoints = new EnlargedPoints(newScan);
    for (Area y : enlargedPoints.collectionOfAreas) {
      for (Area x : enlargedMeans.collectionOfAreas) {
        if (x.intersects(y.getBounds2D())) {
          x.intersect(y);
          // to be continued
        }
      }
    }
    return 0;
  }
}

enum GokartRayLogExport {
  ;
  public static void main(String[] args) throws IOException {
    final String channel = VelodyneLcmChannels.ray(VelodyneModel.VLP16, "center");
    Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    Handler handler = new Handler();
    vlp16LcmHandler.lidarAngularFiringCollector.addListener(handler.lidarRayBlockListener);
    // RotationalHistogram listener = new RotationalHistogram();
    // vlp16Decoder.addRayListener(listener);
    // TemporalHistogram temporalHistogram = new TemporalHistogram();
    // vlp16Decoder.addRayListener(temporalHistogram);
    // PlanarHistogram planarHistogram = new PlanarHistogram();
    // vlp16Decoder.addRayListener(planarHistogram);
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
