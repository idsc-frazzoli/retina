//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides blob object in image space
public class DavisSingleBlob {
  // camera parameters
  private static final int WIDTH = 240; // maybe import those values from other file?
  private static final int HEIGHT = 180;
  // blob parameters
  private final float[] initPos;
  private float[] pos;
  private float[][] covariance;
  private float activity;
  private boolean layerID; // true for active layer, false for hidden layer
  private float currentScore;

  // initialize with position and covariance
  DavisSingleBlob(float initialX, float initialY, float initVariance) {
    initPos = new float[] { initialX, initialY };
    pos = new float[] { initialX, initialY };
    covariance = new float[][] { { initVariance, 0 }, { 0, initVariance } };
    layerID = false;
    activity = 0.0f;
    currentScore = 0.0f;
  }

  // updates the activity of a blob
  public boolean updateBlobActivity(DavisDvsEvent davisDvsEvent, boolean hasHighestScore, float aUp, float exponential) {
    boolean isPromoted;
    if (hasHighestScore) {
      // if hidden layer blob hits threshold it should be promoted
      isPromoted = !layerID && (activity * exponential + currentScore) > aUp;
      activity = activity * exponential + currentScore;
      return isPromoted;
    }
    activity = activity * exponential;
    isPromoted = false;
    return isPromoted;
  }

  // updates the matching blob
  public void updateBlobParameters(DavisDvsEvent davisDvsEvent, float alphaOne, float alphaTwo) {
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
    // delta covariance
    float deltaXX = (eventPosX - pos[0]) * (eventPosX - pos[0]);
    float deltaXY = (eventPosX - pos[0]) * (eventPosY - pos[1]);
    float deltaYY = (eventPosY - pos[1]) * (eventPosY - pos[1]);
    float[][] deltaCovariance = { { deltaXX, deltaXY }, { deltaXY, deltaYY } };
    // covariance update
    covariance[0][0] = (1 - alphaTwo) * covariance[0][0] + alphaTwo * deltaCovariance[0][0];
    covariance[0][1] = (1 - alphaTwo) * covariance[0][1] + alphaTwo * deltaCovariance[0][1];
    covariance[1][0] = (1 - alphaTwo) * covariance[1][0] + alphaTwo * deltaCovariance[1][0];
    covariance[1][1] = (1 - alphaTwo) * covariance[1][1] + alphaTwo * deltaCovariance[1][1];
    // position update
    pos[0] = (1 - alphaOne) * pos[0] + alphaOne * eventPosX;
    pos[1] = (1 - alphaOne) * pos[1] + alphaOne * eventPosY;
  }

  // calculate score that is based on distance between event and center of probability distribution function
  public float calculateBlobScore(DavisDvsEvent davisDvsEvent) {
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
    // determinant and inverse
    float covarianceDeterminant = covariance[0][0] * covariance[1][1] - covariance[0][1] * covariance[1][0];
    float[][] covarianceInverse = { { covariance[1][1], -covariance[0][1] }, { -covariance[1][0], covariance[0][0] } };
    covarianceInverse[0][0] /= covarianceDeterminant;
    covarianceInverse[0][1] /= covarianceDeterminant;
    covarianceInverse[1][0] /= covarianceDeterminant;
    covarianceInverse[1][1] /= covarianceDeterminant;
    // compute exponent of Gaussian distribution
    float offsetX = eventPosX - pos[0];
    float offsetY = eventPosY - pos[1];
    float[] intermediate = { covarianceInverse[0][0] * offsetX + covarianceInverse[0][1] * offsetY,
        covarianceInverse[1][0] * offsetX + covarianceInverse[1][1] * offsetY };
    float exponent = (float) (-0.5 * (offsetX * intermediate[0] + offsetY * intermediate[1]));
    currentScore = (float) (1 / (2 * Math.PI) * 1 / Math.sqrt(covarianceDeterminant) * Math.exp(exponent));
    return currentScore;
  }

  public boolean updateAttractionEquation(float alphaAttr, float dRep) {
    boolean reset;
    float posDiff = (float) Math.sqrt((pos[0] - initPos[0]) * (pos[0] - initPos[0]) + (pos[1] - initPos[1]) * (pos[1] - initPos[1]));
    if (posDiff > dRep) {
      pos = initPos;
      reset = true;
    } else {
      pos[0] = pos[0] + alphaAttr * (initPos[0] - pos[0]);
      pos[1] = pos[1] + alphaAttr * (initPos[1] - pos[1]);
      reset = false;
    }
    return reset;
  }

  public void updateRepulsionEquation(float alphaRep, float dRep, DavisSingleBlob otherBlob) {
    float[] otherPos = otherBlob.getPos();
    float posDiff = (float) Math.sqrt((pos[0] - otherPos[0]) * (pos[0] - otherPos[0]) + (pos[1] - otherPos[1]) * (pos[1] - otherPos[1]));
    float exponential = (float) (Math.exp(posDiff / dRep));
    // blob is not repulsed if other blob has zero activity
    // TODO what should happen if both blobs have zero activity?
    if (otherBlob.getActivity() != 0) {
      pos[0] = pos[0] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
          / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[0] - pos[0]);
      pos[1] = pos[1] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
          / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[1] - pos[1]);
    }
  }

  public boolean blobPromotion(float aUp) {
    layerID = activity > aUp;
    return layerID;
  }

  // if blob closer than small semiaxis to the boarder, return true
  public boolean isOutOfBounds(float numberSigmas) {
    float boundPointLeft = pos[0] - numberSigmas * this.getSemiAxes()[1];
    float boundPointRight = pos[0] + numberSigmas * this.getSemiAxes()[1];
    float boundPointUp = pos[1] - numberSigmas * this.getSemiAxes()[1];
    float boundPointDown = pos[1] + numberSigmas * this.getSemiAxes()[1];
    return boundPointLeft < 0 || boundPointRight > (WIDTH - 1) || boundPointUp < 0 || boundPointDown > HEIGHT;
  }

  // angle at which the ellipse is rotated
  public float getRotAngle() {
    return (float) (0.5 * Math.atan(2 * covariance[0][1] / (covariance[1][1] - covariance[0][0])));
  }

  public float[] getSemiAxes() {
    double root = Math.sqrt((covariance[0][0] - covariance[1][1]) * (covariance[0][0] - covariance[1][1]) + 4 * covariance[0][1] * covariance[0][1]);
    float largeAxis = (float) (Math.sqrt(0.5 * (covariance[0][0] + covariance[1][1] + root)));
    float smallAxis = (float) (Math.sqrt(0.5 * (covariance[0][0] + covariance[1][1] - root)));
    return new float[] { largeAxis, smallAxis };
  }

  public void setLayerID(boolean layerID) {
    this.layerID = layerID;
  }

  public boolean getLayerID() {
    return layerID;
  }

  public float getActivity() {
    return activity;
  }

  public float[] getPos() {
    return pos;
  }

  public float[] getInitPos() {
    return initPos;
  }

  public float[][] getCovariance() {
    return covariance;
  }

  public float getScore() {
    return currentScore;
  }
}
