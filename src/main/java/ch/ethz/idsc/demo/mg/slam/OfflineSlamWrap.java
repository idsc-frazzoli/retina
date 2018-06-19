// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// A SLAM algorithm "wrapper" to run the algorithm offline. DVS Events, wheel odometry 
// and lidar pose are provided to the SLAM algorithm
public class OfflineSlamWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final GokartPoseOdometry gokartOdometryPose = GokartPoseOdometry.create();
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final SlamProvider slamProvider;

  public OfflineSlamWrap(PipelineConfig pipelineConfig) {
    slamProvider = new SlamProvider(pipelineConfig, gokartOdometryPose, gokartLidarPose);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
  }

  // decode DavisDvsEvents, RimoGetEvents and GokartPoseEvents
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
    if (channel.equals(RimoLcmServer.CHANNEL_GET)) {
      gokartOdometryPose.getEvent(new RimoGetEvent(byteBuffer));
    }
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    }
  }
}
