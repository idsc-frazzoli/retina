// code by vc
package ch.ethz.idsc.demo.vc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum ClusterEvaluation {
  ;
  private static final String CHANNEL_VLP16 = VelodyneLcmChannels.ray(VelodyneModel.VLP16, "center");

  public static void main(String[] args) throws IOException {
    Tensor minPoints = Tensors.empty();
    Tensor epsilon = Tensors.empty();
    for (int i = 2; i < 10; i++) {
      minPoints.append(RealScalar.of(i));
      epsilon.append(RealScalar.of(0.01 + 0.005 * i));
    }
    for (Tensor eps : epsilon) {
      for (Tensor minPts : minPoints) {
        System.out.println(minPts);
        ClusterConfig clusterConfig = new ClusterConfig();
        clusterConfig.epsilon = Quantity.of(eps.Get(), SI.METER);
        clusterConfig.minPoints = minPts.Get();
        ClusterAreaEvaluationListener clusterEvaluationListener = new ClusterAreaEvaluationListener(clusterConfig);
        Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
        vlp16LcmHandler.lidarAngularFiringCollector.addListener(clusterEvaluationListener.lidarClustering);
        OfflineLogListener offlineLogListener = new OfflineLogListener() {
          @Override
          public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
            if (channel.equals(CHANNEL_VLP16))
              vlp16LcmHandler.velodyneDecoder.lasers(byteBuffer);
            else //
            if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
              GokartPoseEvent gpe = new GokartPoseEvent(byteBuffer);
              clusterEvaluationListener.setPose(gpe.getPose());
            }
          }
        };
        File file = HomeDirectory.file("Desktop/ETHZ/log/trimmed3.lcm");
        if (UserName.is("datahaki"))
          file = HomeDirectory.file("gokart", "pedestrian", "20180412T163855", "log.lcm");
        OfflineLogPlayer.process(file, offlineLogListener);
      }
    }
  }
}
