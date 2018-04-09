// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides blob object in image space
public class DavisSingleBlob {
  // camera parameters
  private static final int WIDTH = 240; // maybe import those values from other file?
  private static final int HEIGHT = 180;
  // blob parameters
  private final float[] initPos;
  private final float[] pos;
  private final double[][] covariance;
  private float activity;
  private boolean layerID; // true for active layer, false for hidden layer
  private float currentScore;

  // initialize with position and covariance
  DavisSingleBlob(float initialX, float initialY, float initVariance) {
    initPos = new float[] { initialX, initialY };
    pos = new float[] { initialX, initialY };
    covariance = new double[][] { { initVariance, 0 }, { 0, initVariance } };
    layerID = false;
    activity = 0.0f;
    currentScore = 0.0f;
  }

  // updates the activity of a blob
  public boolean updateBlobActivity(boolean hasHighestScore, float aUp, float exponential) {
    boolean isPromoted;
    if (hasHighestScore) {
      // if hidden layer blob hits threshold it should be promoted
      float potentialActivity = activity * exponential + currentScore;
      isPromoted = !layerID && potentialActivity > aUp;
      activity = potentialActivity;
      return isPromoted;
    }
    activity = activity * exponential;
    isPromoted = false;
    return isPromoted;
  }

  // updates parameters of matching blob
  public void updateBlobParameters(DavisDvsEvent davisDvsEvent, float alphaOne, float alphaTwo) {
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
    // position update
    pos[0] = alphaOne * pos[0] + (1 - alphaOne) * eventPosX;
    pos[1] = alphaOne * pos[1] + (1 - alphaOne) * eventPosY;
    // delta covariance
    float deltaXX = (eventPosX - pos[0]) * (eventPosX - pos[0]);
    float deltaXY = (eventPosX - pos[0]) * (eventPosY - pos[1]);
    float deltaYY = (eventPosY - pos[1]) * (eventPosY - pos[1]);
    float[][] deltaCovariance = { { deltaXX, deltaXY }, { deltaXY, deltaYY } };
    // covariance update
    covariance[0][0] = alphaTwo * covariance[0][0] + (1 - alphaTwo) * deltaCovariance[0][0];
    covariance[0][1] = alphaTwo * covariance[0][1] + (1 - alphaTwo) * deltaCovariance[0][1];
    covariance[1][0] = alphaTwo * covariance[1][0] + (1 - alphaTwo) * deltaCovariance[1][0];
    covariance[1][1] = alphaTwo * covariance[1][1] + (1 - alphaTwo) * deltaCovariance[1][1];
  }

  // scoring function based on Gaussian distribution
  public float gaussianBlobScore(DavisDvsEvent davisDvsEvent) {
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
    // determinant and inverse
    double covarianceDeterminant = covariance[0][0] * covariance[1][1] - covariance[0][1] * covariance[1][0];
    double[][] covarianceInverse = { { covariance[1][1], -covariance[0][1] }, { -covariance[1][0], covariance[0][0] } };
    covarianceInverse[0][0] /= covarianceDeterminant;
    covarianceInverse[0][1] /= covarianceDeterminant;
    covarianceInverse[1][0] /= covarianceDeterminant;
    covarianceInverse[1][1] /= covarianceDeterminant;
    // compute exponent of Gaussian distribution
    float offsetX = eventPosX - pos[0];
    float offsetY = eventPosY - pos[1];
    double[] intermediate = { covarianceInverse[0][0] * offsetX + covarianceInverse[0][1] * offsetY,
        covarianceInverse[1][0] * offsetX + covarianceInverse[1][1] * offsetY };
    float exponent = (float) (-0.5 * (offsetX * intermediate[0] + offsetY * intermediate[1]));
    currentScore = (float) (1 / (2 * Math.PI) * 1 / Math.sqrt(covarianceDeterminant) * Math.exp(exponent));
    return currentScore;
  }

  // EXPERIMENTAL STATUS function
  // scoring function based on Gabor filters - implementation based on same paper as tracking algorithm
  public float gaborBlobScore(DavisDvsEvent davisDvsEvent) {
    double sigma = 3;
    double gamma = sigma / 15;
    double lambda = 4 * sigma;
    double theta = Math.PI / 2;
    double xU = (davisDvsEvent.x - pos[0]) * Math.cos(theta) + (davisDvsEvent.y - pos[1]) * Math.sin(theta);
    double yU = -(davisDvsEvent.x - pos[0]) * Math.sin(theta) + (davisDvsEvent.y - pos[1]) * Math.cos(theta);
    currentScore = (float) Math.exp((xU * xU + gamma * gamma * yU * yU) / (2 * sigma * sigma) * Math.cos(2 * Math.PI * xU / lambda));
    return currentScore;
  }

  public boolean updateAttractionEquation(float alphaAttr, float dRep) {
    boolean reset;
    float posDiff = (float) Math.sqrt((pos[0] - initPos[0]) * (pos[0] - initPos[0]) + (pos[1] - initPos[1]) * (pos[1] - initPos[1]));
    if (posDiff > dRep) {
      pos[0] = initPos[0];
      pos[1] = initPos[1];
      reset = true;
    } else {
      pos[0] = pos[0] + alphaAttr * (initPos[0] - pos[0]);
      pos[1] = pos[1] + alphaAttr * (initPos[1] - pos[1]);
      reset = false;
    }
    return reset;
  }

  // function is not tested!
  // public void updateRepulsionEquation(float alphaRep, float dRep, DavisSingleBlob otherBlob) {
  // float[] otherPos = otherBlob.getPos();
  // float posDiff = (float) Math.sqrt((pos[0] - otherPos[0]) * (pos[0] - otherPos[0]) + (pos[1] - otherPos[1]) * (pos[1] - otherPos[1]));
  // float exponential = (float) (Math.exp(posDiff / dRep));
  // // blob is not repulsed if other blob has zero activity
  // // TODO what should happen if both blobs have zero activity?
  // if (otherBlob.getActivity() != 0) {
  // pos[0] = pos[0] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
  // / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[0] - pos[0]);
  // pos[1] = pos[1] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
  // / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[1] - pos[1]);
  // }
  // }
  //
  public boolean blobPromotion(float aUp) {
    layerID = activity > aUp;
    return layerID;
  }

  // if blob is too close to boundary, return true
  public boolean isOutOfBounds(int boundaryDistance) {
    float boundPointLeft = pos[0] - boundaryDistance;
    float boundPointRight = pos[0] + boundaryDistance;
    float boundPointUp = pos[1] - boundaryDistance;
    float boundPointDown = pos[1] + boundaryDistance;
    return boundPointLeft < 0 || boundPointRight > (WIDTH - 1) || boundPointUp < 0 || boundPointDown > HEIGHT;
  }

  public void increaseBlobSize(float enlargement) {
    covariance[0][0] *= enlargement;
    covariance[0][1] *= enlargement;
    covariance[1][0] *= enlargement;
    covariance[1][1] *= enlargement;
  }

  // return a size metric, currently trace of matrix
  public double getSizeMetric() {
    return covariance[0][0] + covariance[1][1];
  }

  // length of semiaxes equal to eigenvalues CONFIRMED to be same as tensor.eigenvalues method
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

  public double[][] getCovariance() {
    return covariance;
  }

  public float getScore() {
    return currentScore;
  }
}
