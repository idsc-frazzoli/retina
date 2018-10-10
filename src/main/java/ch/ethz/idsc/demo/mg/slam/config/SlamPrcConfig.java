// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;

/** parameters for the map processing steps of the SLAM algorithm */
public class SlamPrcConfig {
  public Scalar mapThreshold;
  public Scalar visibleBoxXMin;
  public Scalar visibleBoxXMax;
  public Scalar visibleBoxYHalfWidth;
  public Scalar deltaPosThreshold;
  public Scalar distanceThreshold;
  public Scalar validPointsThreshold;
  public Scalar curvatureThreshold;
  public Scalar iterations;
  public Scalar curveFactor;
  public Scalar extrapolationDistance;
  public Scalar numberOfPoints;
  public Scalar alphaCurvature;
  public Scalar extractionPoints;
  public Scalar alphaHeading;
  public Scalar lookAhead;

  /** @return unitless look ahead distance with interpretation in meters */
  public Scalar lookAheadMeter() {
    return Magnitude.METER.apply(lookAhead);
  }
}
