// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/* package */ abstract class AbstractSlamStep implements DavisDvsListener {
  protected final SlamContainer slamContainer;
  protected final SlamImageToGokart slamImageToGokart;

  protected AbstractSlamStep(SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    this.slamContainer = slamContainer;
    this.slamImageToGokart = slamImageToGokart;
  }
}
