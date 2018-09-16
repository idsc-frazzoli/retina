// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

/** abstract base class for SLAM algorithm modules that involve processing of detected
 * feature points */
/* package */ abstract class AbstractSlamCurveStep implements CurveListener {
  protected final SlamCurveContainer slamCurveContainer;

  AbstractSlamCurveStep(SlamCurveContainer slamCurveContainer) {
    this.slamCurveContainer = slamCurveContainer;
  }
}
