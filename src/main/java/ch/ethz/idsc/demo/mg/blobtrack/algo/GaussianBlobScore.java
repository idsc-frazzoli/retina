// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackObj;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/* package */ enum GaussianBlobScore implements BlobScore {
  INSTANCE;
  // ---
  @Override
  public double evaluate(BlobTrackObj blobTrackObj, DavisDvsEvent davisDvsEvent) {
    double[][] covariance = blobTrackObj.getCovariance();
    float[] pos = blobTrackObj.getPos();
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
    // determinant and inverse
    double covarianceDeterminant = covariance[0][0] * covariance[1][1] - covariance[0][1] * covariance[1][0];
    double reciprocal = 1 / covarianceDeterminant;
    double[][] covarianceInverse = { //
        { +covariance[1][1] * reciprocal, -covariance[0][1] * reciprocal }, //
        { -covariance[1][0] * reciprocal, +covariance[0][0] * reciprocal } };
    // compute exponent of Gaussian distribution
    float offsetX = eventPosX - pos[0];
    float offsetY = eventPosY - pos[1];
    double[] intermediate = { //
        covarianceInverse[0][0] * offsetX + covarianceInverse[0][1] * offsetY, //
        covarianceInverse[1][0] * offsetX + covarianceInverse[1][1] * offsetY };
    double exponent = -0.5 * (offsetX * intermediate[0] + offsetY * intermediate[1]);
    return 1 / (2 * Math.PI) * 1 / Math.sqrt(covarianceDeterminant) * Math.exp(exponent);
  }
}
