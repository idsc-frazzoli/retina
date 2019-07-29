// code by mg
package ch.ethz.idsc.retina.app.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.lcm.davis.DvsLcmClient;
import ch.ethz.idsc.retina.app.filter.AbstractFilterHandler;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.app.slam.vis.SlamViewer;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** base class to initialize the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf */
public abstract class AbstractSlamWrap implements DavisDvsListener, StartAndStoppable {
  protected final DvsLcmClient dvsLcmClient;
  protected final GokartPoseOdometryDemo gokartPoseOdometryDemo = GokartPoseOdometryDemo.create();
  // SLAM modules below
  protected final SlamCoreContainer slamCoreContainer;
  protected final SlamPrcContainer slamPrcContainer;
  protected final AbstractFilterHandler abstractFilterHandler;
  protected final SlamViewer slamViewer;
  protected GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  protected final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  // ---
  protected boolean triggered;

  protected AbstractSlamWrap() {
    dvsLcmClient = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.dvsLcmClient;
    dvsLcmClient.addDvsListener(this);
    slamCoreContainer = new SlamCoreContainer();
    slamPrcContainer = new SlamPrcContainer(slamCoreContainer);
    abstractFilterHandler = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.createBackgroundActivityFilter();
    slamViewer = new SlamViewer(slamCoreContainer, slamPrcContainer);
  }

  @Override // from StartAndStoppable
  public final void start() {
    protected_start();
    dvsLcmClient.startSubscriptions();
  }

  @Override // from StartAndStoppable
  public final void stop() {
    dvsLcmClient.stopSubscriptions();
    slamViewer.stop();
    abstractFilterHandler.stopStopableListeners();
    protected_stop();
  }

  protected abstract void protected_start();

  protected abstract void protected_stop();

  /** the SLAM algorithm is initialized only if the DAVIS publishes the event stream and the gokartLidarPose is available.
   * visualization task is initialized as well at this instant */
  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!triggered)
      if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
        triggered = true;
        dvsLcmClient.addDvsListener(abstractFilterHandler);
        dvsLcmClient.addDvsListener(slamViewer);
        SlamWrapUtil.initialize(slamCoreContainer, slamPrcContainer, //
            abstractFilterHandler, gokartPoseEvent, gokartPoseOdometryDemo);
        slamViewer.start();
        dvsLcmClient.removeDvsListener(this);
      }
  }

  public SlamPrcContainer getSlamPrcContainer() {
    return slamPrcContainer;
  }
}
