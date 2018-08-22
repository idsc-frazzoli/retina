// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

/** wrapper to run SLAM algorithm with offline log files */
public class OfflineSlamWrap implements OfflineLogListener {
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final AbstractFilterHandler filterHandler;
  private final SlamProvider slamProvider;
  private final SlamViewer slamViewer;

  public OfflineSlamWrap(SlamConfig slamConfig) {
    filterHandler = new BackgroundActivityFilter(slamConfig.davisConfig);
    davisDvsDatagramDecoder.addDvsListener(filterHandler);
    slamProvider = new SlamProvider(slamConfig, filterHandler, gokartLidarPose);
    slamViewer = new SlamViewer(slamConfig, slamProvider.getSlamContainer(), gokartLidarPose);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR))
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    if (slamProvider.getSlamContainer().getActive() && channel.equals("davis240c.overview.dvs"))
      davisDvsDatagramDecoder.decode(byteBuffer);
    // TODO triggering module required
    if (!slamProvider.getSlamContainer().getActive() && gokartLidarPose.getPose() != GokartPoseLocal.INSTANCE.getPose()) {
      slamProvider.getSlamContainer().initialize(gokartLidarPose.getPose());
    }
  }
}
