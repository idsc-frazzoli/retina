// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackObj;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// TODO MG state reference to formula
// TODO how to incorporate event polarity?
/* package */ enum GaborBlobScore implements BlobScore {
  INSTANCE;
  // ---
  private static final double SIGMA = 3;
  private static final double GAMMA = SIGMA / 15;
  private static final double LAMBDA = 4 * SIGMA;
  private static final double THETA = Math.PI / 2;
  // FIXME MG formula does not make sense COS_THETA==0
  private static final double COS_THETA = Math.cos(THETA); // == 0
  private static final double SIN_THETA = Math.sin(THETA); // == 1

  @Override
  public double evaluate(BlobTrackObj blobTrackObj, DavisDvsEvent davisDvsEvent) {
    float[] pos = blobTrackObj.getPos();
    double xU = +(davisDvsEvent.x - pos[0]) * COS_THETA + (davisDvsEvent.y - pos[1]) * SIN_THETA;
    double yU = -(davisDvsEvent.x - pos[0]) * SIN_THETA + (davisDvsEvent.y - pos[1]) * COS_THETA;
    return Math.exp((xU * xU + GAMMA * GAMMA * yU * yU) / (2 * SIGMA * SIGMA) * Math.cos(2 * Math.PI * xU / LAMBDA));
  }
}
