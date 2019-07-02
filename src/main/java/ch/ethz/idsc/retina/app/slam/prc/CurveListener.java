// code by mg
package ch.ethz.idsc.retina.app.slam.prc;

/** listener to process the feature points detected by the SLAM algorithm */
@FunctionalInterface
interface CurveListener {
  /** to be called after SlamMapProcessing detected new set of feature points */
  void process();
}
