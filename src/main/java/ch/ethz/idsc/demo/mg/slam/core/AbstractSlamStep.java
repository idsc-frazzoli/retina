// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** abstract base class for SLAM algorithm modules. All information shared between steps
 * is passed through a {@link SlamCoreContainer} object */
/* package */ abstract class AbstractSlamStep implements DavisDvsListener {
  protected final SlamCoreContainer slamCoreContainer;

  AbstractSlamStep(SlamCoreContainer slamCoreContainer) {
    this.slamCoreContainer = slamCoreContainer;
  }
}
