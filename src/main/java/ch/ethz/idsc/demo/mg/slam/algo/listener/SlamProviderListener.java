// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.List;

import ch.ethz.idsc.demo.mg.filter.AbstractFilterHandler;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;

/** implementation of the SLAM algorithm
 * "simultaneous localization and mapping for event-based vision systems"
 * by David Weikersdorfer, Raoul Hoffmann, and Joerg Conradt
 * https://mediatum.ub.tum.de/doc/1191908/1191908.pdf */
public class SlamProviderListener {
  private final SlamContainer slamContainer;
  private final List<DavisDvsListener> listeners;

  public SlamProviderListener(SlamConfig slamConfig, AbstractFilterHandler filterHandler) {
    slamContainer = new SlamContainer(slamConfig);
    listeners = SlamAlgoConfig.standardConfig(slamConfig, slamContainer);
    listeners.forEach(filterHandler::addListener);
  }

  public SlamContainer getSlamContainer() {
    return slamContainer;
  }
}
