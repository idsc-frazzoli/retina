// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;

/** abstract base class for SLAM algorithm modules that involve processing of detected
 * feature points */
/* package */ abstract class AbstractSlamCurveStep implements CurveListener {
  protected final SlamPrcContainer slamPrcContainer;

  AbstractSlamCurveStep(SlamPrcContainer slamPrcContainer) {
    this.slamPrcContainer = slamPrcContainer;
  }
}
