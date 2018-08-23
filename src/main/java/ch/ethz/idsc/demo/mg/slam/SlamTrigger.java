// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.filter.BackgroundActivityFilter;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;

/** triggers the initialization of the SLAM algorithm */
public class SlamTrigger implements DavisDvsListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder;
  private final GokartPoseOdometryDemo gokartPoseOdometry;
  private final GokartPoseInterface gokartLidarPose;
  private final AbstractFilterHandler filterHandler;
  private final SlamProvider slamProvider;
  private final SlamConfig slamConfig;
  // ---
  private boolean triggered;

  public SlamTrigger(SlamConfig slamConfig, GokartPoseInterface gokartLidarPose, DavisDvsDatagramDecoder davisDvsdatagramDecoder, //
      GokartPoseOdometryDemo gokartPoseOdometry) {
    this.davisDvsDatagramDecoder = davisDvsdatagramDecoder;
    this.gokartPoseOdometry = gokartPoseOdometry;
    this.slamConfig = slamConfig;
    this.gokartLidarPose = gokartLidarPose;
    filterHandler = new BackgroundActivityFilter(slamConfig.davisConfig);
    slamProvider = new SlamProvider(slamConfig, filterHandler, gokartLidarPose, gokartPoseOdometry);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!triggered) {
      if (gokartLidarPose.getPose() != GokartPoseLocal.INSTANCE.getPose()) {
        setupSlamAlgorithm();
        triggered = true;
      }
    }
  }

  private void setupSlamAlgorithm() {
    davisDvsDatagramDecoder.addDvsListener(filterHandler);
    slamProvider.getSlamContainer().initialize(gokartLidarPose.getPose());
    gokartPoseOdometry.setPose(gokartLidarPose.getPose());
    SlamViewer slamViewer = new SlamViewer(slamConfig, slamProvider.getSlamContainer(), gokartLidarPose);
  }
}
