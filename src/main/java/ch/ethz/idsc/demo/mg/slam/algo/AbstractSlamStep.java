// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** abstract base class for SLAM algorithm modules. All information shared between steps
 * is passed through a {@link SlamContainer} object */
/* package */ abstract class AbstractSlamStep implements DavisDvsListener {
  protected final SlamContainer slamContainer;

  AbstractSlamStep(SlamContainer slamContainer) {
    this.slamContainer = slamContainer;
  }
}
