// code by jph
package ch.ethz.idsc.demo.vc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.sca.Increment;

class Handler {
  private ClusterCollection collection = new ClusterCollection();
  /** LidarRayBlockListener to be subscribed after LidarRender */
  LidarRayBlockListener lidarRayBlockListener = new LidarRayBlockListener() {
    @Override
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
      System.out.println("we have clusters" + collection.collection.size());
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
      UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
      Tensor state = Tensors.fromString("{46.965741254102845[m], 48.42802931327099[m], 1.1587704741034797}");
      unknownObstaclePredicate.setPose(state);
      Tensor newScan = Tensor.of(points.stream() //
          .filter(unknownObstaclePredicate::isObstacle) //
          .map(point -> point.extract(0, 2))); // only x,y matter
      if (!Tensors.isEmpty(newScan)) {
        synchronized (collection) {
          ClusterConfig.GLOBAL.elkiDBSCANTracking(collection, newScan);
        }
      } else
        System.err.println("scan is empty");
    }
  };
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
      }
    };
    File file = UserHome.file("/Desktop/ETHZ/log/20180412T163855_7e5b46c2.lcm.00");
    OfflineLogPlayer.process(file, offlineLogListener);
  }
}
