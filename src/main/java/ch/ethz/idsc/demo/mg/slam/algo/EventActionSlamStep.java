// code by mg, jph
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** for SLAM algorithm modules that execute action for each incoming event */
/* package */ abstract class EventActionSlamStep extends AbstractSlamStep {
  EventActionSlamStep(SlamContainer slamContainer) {
    super(slamContainer);
  }

  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    davisDvsAction();
  }

  /** function invoked for every event */
  abstract void davisDvsAction();
}
