// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewerNew;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

public class OfflineSlamWrapNew implements OfflineLogListener {
  private final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final AbstractFilterHandler filterHandler;
  private final SlamProviderListener slamProvider;
  private final SlamViewerNew slamViewer;

  public OfflineSlamWrapNew(SlamConfig slamConfig) {
    filterHandler = new BackgroundActivityFilter(slamConfig.davisConfig);
    davisDvsDatagramDecoder.addDvsListener(filterHandler);
    slamProvider = new SlamProviderListener(slamConfig, filterHandler);
    slamViewer = new SlamViewerNew(slamConfig, slamProvider.getSlamContainer(), gokartLidarPose);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR))
      gokartLidarPose.getEvent(new GokartPoseEvent(byteBuffer));
    if (slamProvider.getSlamContainer().getActive() && channel.equals("davis240c.overview.dvs"))
      davisDvsDatagramDecoder.decode(byteBuffer);
    // TODO HAAAACK
    if (!slamProvider.getSlamContainer().getActive() && gokartLidarPose.getPose() != GokartPoseLocal.INSTANCE.getPose()) {
      slamProvider.getSlamContainer().setActive(true);
      slamProvider.getSlamContainer().initialize(gokartLidarPose.getPose());
    }
  }
}
