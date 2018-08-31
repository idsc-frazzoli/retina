// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.slam.vis.SlamViewer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;

/** triggers the initialization of the SLAM algorithm */
public class SlamTrigger implements DavisDvsListener {
  private final SlamConfig slamConfig;
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder;
  private final GokartPoseOdometryDemo gokartPoseOdometryDemo;
  private final GokartPoseInterface gokartPoseInterface;
  private final AbstractFilterHandler abstractFilterHandler;
  private final SlamProvider slamProvider;
  // ---
  private boolean triggered;

  public SlamTrigger( //
      SlamConfig slamConfig, //
      GokartPoseInterface gokartPoseInterface, //
      DavisDvsDatagramDecoder davisDvsdatagramDecoder, //
      GokartPoseOdometryDemo gokartPoseOdometryDemo) {
    this.slamConfig = slamConfig;
    this.gokartPoseInterface = gokartPoseInterface;
    this.davisDvsDatagramDecoder = davisDvsdatagramDecoder;
    this.gokartPoseOdometryDemo = gokartPoseOdometryDemo;
    abstractFilterHandler = slamConfig.davisConfig.createBackgroundActivityFilter();
    slamProvider = new SlamProvider(slamConfig, abstractFilterHandler, gokartPoseInterface, gokartPoseOdometryDemo);
  }

  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!triggered)
      if (!gokartPoseInterface.getPose().equals(GokartPoseLocal.INSTANCE.getPose())) {
        setupSlamAlgorithm();
        triggered = true;
        // TODO JPH find a way to unsubscribe this slam trigger from dvs events
      }
  }

  private void setupSlamAlgorithm() {
    davisDvsDatagramDecoder.addDvsListener(abstractFilterHandler);
    slamProvider.getSlamContainer().initialize(gokartPoseInterface.getPose());
    gokartPoseOdometryDemo.setPose(gokartPoseInterface.getPose());
    SlamViewer slamViewer = new SlamViewer(slamConfig, slamProvider.getSlamContainer(), gokartPoseInterface);
    davisDvsDatagramDecoder.addDvsListener(slamViewer);
  }

  public SlamContainer getSlamContainer() {
    return slamProvider.getSlamContainer();
  }
}
