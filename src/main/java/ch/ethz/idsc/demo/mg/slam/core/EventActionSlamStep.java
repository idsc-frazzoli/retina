// code by mg, jph
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** for SLAM algorithm modules that execute action for each incoming event */
/* package */ abstract class EventActionSlamStep extends AbstractSlamStep {
  EventActionSlamStep(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
  }

  @Override // from DavisDvsListener
  public final void davisDvs(DavisDvsEvent davisDvsEvent) {
    davisDvsAction();
  }

  /** function invoked for every event */
  abstract void davisDvsAction();
}
