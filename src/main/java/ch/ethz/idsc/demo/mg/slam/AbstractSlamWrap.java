// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmLidar;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** base class to initialize the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf */
public abstract class AbstractSlamWrap implements DavisDvsListener, StartAndStoppable {
  protected final DavisLcmClient davisLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  protected final GokartPoseLcmLidar gokartLidarPose = new GokartPoseLcmLidar();
  protected final GokartPoseOdometryDemo gokartOdometryPose = GokartPoseOdometryDemo.create();
  // SLAM modules below
  protected final SlamCoreConfig slamConfig;
  protected final SlamCoreContainer slamContainer;
  protected final SlamPrcContainer slamCurveContainer;
  protected final AbstractFilterHandler abstractFilterHandler;
  protected final SlamViewer slamViewer;
  // ---
  protected boolean triggered;

  protected AbstractSlamWrap(SlamCoreConfig slamConfig) {
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(this);
    this.slamConfig = slamConfig;
    slamContainer = new SlamCoreContainer(slamConfig);
    slamCurveContainer = new SlamPrcContainer(slamContainer);
    abstractFilterHandler = slamConfig.davisConfig.createBackgroundActivityFilter();
    slamViewer = new SlamViewer(slamConfig, slamContainer, slamCurveContainer, gokartLidarPose);
  }

  @Override // from StartAndStoppable
  public final void start() {
    protected_start();
    gokartLidarPose.gokartPoseLcmClient.startSubscriptions();
    davisLcmClient.startSubscriptions();
  }

  @Override // from StartAndStoppable
  public final void stop() {
    gokartLidarPose.gokartPoseLcmClient.stopSubscriptions();
    davisLcmClient.stopSubscriptions();
    slamViewer.stop();
    abstractFilterHandler.stopStoppableListeners();
    protected_stop();
  }

  protected abstract void protected_start();

  protected abstract void protected_stop();

  /** the SLAM algorithm is initialized only if the DAVIS publishes the event stream and the gokartLidarPose is available.
   * visualization task is initialized as well at this instant */
  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!triggered)
      if (!gokartLidarPose.getPose().equals(GokartPoseLocal.INSTANCE.getPose())) {
        davisLcmClient.davisDvsDatagramDecoder.addDvsListener(abstractFilterHandler);
        davisLcmClient.davisDvsDatagramDecoder.addDvsListener(slamViewer);
        SlamWrapUtil.initialize(slamConfig, slamContainer, slamCurveContainer, //
            abstractFilterHandler, gokartLidarPose, gokartOdometryPose);
        slamViewer.start();
        triggered = true;
        // TODO JPH find a way to unsubscribe once it has been triggered
      }
  }

  // public SlamContainer getSlamContainer() {
  // return slamContainer;
  // }
  public SlamPrcContainer getSlamCurveContainer() {
    return slamCurveContainer;
  }
}
