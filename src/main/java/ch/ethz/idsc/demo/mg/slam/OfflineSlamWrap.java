// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// provides a SLAM algorithm "wrapper" to test the algorithm with log files
// TODO set up the lidar pose providers and wheel odometry providers for usage in SlamProvider
public class OfflineSlamWrap implements OfflineLogListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final GokartPoseOdometry gokartPoseOdometry = GokartPoseOdometry.create();
  private final SlamProvider slamProvider;

  public OfflineSlamWrap(PipelineConfig pipelineConfig) {    
    rimoGetLcmClient.addListener(gokartPoseOdometry);
    rimoGetLcmClient.startSubscriptions();
    slamProvider = new SlamProvider(pipelineConfig, gokartPoseOdometry);
    davisDvsDatagramDecoder.addDvsListener(slamProvider);
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
    // how to publish the RimoGetEvent to GokartPoseOdometry here?
  }
}
