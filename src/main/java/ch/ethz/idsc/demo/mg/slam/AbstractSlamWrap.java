// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;

/** base class to initialize the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf */
public abstract class AbstractSlamWrap implements DavisDvsListener {
  protected final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  protected final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  protected final GokartPoseOdometryDemo gokartOdometryPose = GokartPoseOdometryDemo.create();
  protected final SlamConfig slamConfig;
  protected final SlamContainer slamContainer;
  protected final AbstractFilterHandler abstractFilterHandler;
  protected final SlamViewer slamViewer;
  // ---
  protected boolean triggered;

  protected AbstractSlamWrap(SlamConfig slamConfig) {
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(this);
    this.slamConfig = slamConfig;
    slamContainer = new SlamContainer(slamConfig);
    abstractFilterHandler = slamConfig.davisConfig.createBackgroundActivityFilter();
    slamViewer = new SlamViewer(slamConfig, slamContainer, gokartLidarPose);
  }

  /** the SLAM algorithm is initialized only if the DAVIS publishes the event stream and the gokartLidarPose is available.
   * visualization task is initialized as well at this instant */
  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!triggered)
      if (!gokartLidarPose.getPose().equals(GokartPoseLocal.INSTANCE.getPose())) {
        davisLcmClient.davisDvsDatagramDecoder.addDvsListener(abstractFilterHandler);
        davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamViewer);
        SlamWrapUtil.initialize(slamConfig, slamContainer, abstractFilterHandler, gokartLidarPose, gokartOdometryPose);
        slamViewer.start();
        triggered = true;
        // TODO JPH find a way to unsubscribe once it has been triggered
      }
  }

  public SlamContainer getSlamContainer() {
    return slamContainer;
  }
}
