//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class DavisSingleBlob {
  // camera parameters
  private static final int WIDTH = 240; // maybe import those values from other file?
  private static final int HEIGHT = 180;
  // static fields
  private static int lastTimestamp = 0; // [us]
  // blob parameters
  private final float[] initPos; // float to avoid type cast all the time
  private float[] pos;
  private float[][] covariance;
  private float activity;
  private boolean layerID; // true for active layer, false for hidden layer
  private float currentScore; // store this value because its needed for the activity update

  // initialize with position and covariance
  DavisSingleBlob(float initialX, float initialY, float initVariance) {
    initPos = new float[] { initialX, initialY };
    pos = initPos;
    covariance = new float[][] { { initVariance, 0 }, { 0, initVariance } };
    layerID = false;
    activity = 0.0f;
    currentScore = 0.0f;
  }

  // stores the timestamp of last event
  public static void updateTimestamp(DavisDvsEvent davisDvsEvent) {
    lastTimestamp = davisDvsEvent.time;
  }

  // updates the activity of a blob
  public boolean updateBlobActivity(DavisDvsEvent davisDvsEvent, float tau, boolean hasHighestScore, float aUp) {
    boolean isPromoted;
    float deltaT = (float) (davisDvsEvent.time - lastTimestamp);
    float exponent = deltaT / tau;
    float exponential = (float) Math.exp(-exponent);
    if (hasHighestScore) {
      if (this.activity < aUp && this.activity + currentScore > aUp) {
        isPromoted = true;
      } else {
        isPromoted = false;
      }
      this.activity = this.activity * exponential + currentScore;
      return isPromoted;
    } else {
      this.activity = this.activity * exponential;
      isPromoted = false;
      return isPromoted;
    }
  }

  // updates the blob with a new event that is associated with it
  public void updateBlobParameters(DavisDvsEvent davisDvsEvent, float alphaOne, float alphaTwo) {
    // cast into float
    float eventPosX = (float) davisDvsEvent.x;
    float eventPosY = (float) davisDvsEvent.x;
    // covariance update
    float deltaXX = (eventPosX - pos[0]) * (eventPosX - pos[0]);
    float deltaXY = (eventPosX - pos[0]) * (eventPosY - pos[1]);
    float deltaYY = (eventPosY - pos[1]) * (eventPosY - pos[1]);
    float[][] deltaCovariance = { { deltaXX, deltaXY }, { deltaXY, deltaYY } };
    // this is awful but no matrix library available?
    // covariance update
    covariance[0][0] = (1 - alphaTwo) * covariance[0][0] + alphaTwo * deltaCovariance[0][0];
    covariance[0][1] = (1 - alphaTwo) * covariance[0][1] + alphaTwo * deltaCovariance[0][1];
    covariance[1][0] = (1 - alphaTwo) * covariance[1][0] + alphaTwo * deltaCovariance[1][0];
    covariance[1][1] = (1 - alphaTwo) * covariance[1][1] + alphaTwo * deltaCovariance[1][1];
    // position update
    pos[0] = (1 - alphaOne) * pos[0] + alphaOne * eventPosX;
    pos[1] = (1 - alphaOne) * pos[1] + alphaOne * eventPosY;
  }

  // calculate score that is based on distance between event and center of distribution
  public float calculateBlobScore(DavisDvsEvent davisDvsEvent) {
    // cast into float
    float eventPosX = (float) davisDvsEvent.x;
    float eventPosY = (float) davisDvsEvent.x;
    // determinant
    float covarianceDeterminant = covariance[0][0] * covariance[1][1] - covariance[0][1] * covariance[1][0];
    // compute inverse
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
    currentScore = (float) Math.exp(exponent); // no normalization
    return currentScore;
  }

  public void updateAttractionEquation(float alphaAttr, float dRep) {
    float positionDiff = (float) Math.sqrt((pos[0] - initPos[0]) * (pos[0] - initPos[0]) + (pos[1] - initPos[1]) * (pos[1] - initPos[1]));
    if (positionDiff > dRep) {
      pos = initPos;
    } else {
      pos[0] = pos[0] + alphaAttr * (initPos[0] - pos[0]);
      pos[1] = pos[1] + alphaAttr * (initPos[1] - pos[1]);
    }
  }

  public void updateRepulsionEquation(float alphaRep, float dRep, DavisSingleBlob otherBlob) {
    float[] otherPos = otherBlob.getPos();
    float positionDiff = (float) Math.sqrt((pos[0] - initPos[0]) * (pos[0] - initPos[0]) + (pos[1] - initPos[1]) * (pos[1] - initPos[1]));
    float exponential = (float) (Math.exp(positionDiff / dRep));
    pos[0] = pos[0] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
        / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[0] - pos[0]);
    pos[1] = pos[1] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
        / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[1] - pos[1]);
  }

  public boolean blobPromotion(float aUp) {
    if (activity > aUp) {
      layerID = true;
    } else {
      layerID = false;
    }
    return layerID;
  }

  // checks if the blob is too close to a boarder and if so returns true.
  // TODO we assume that the axes of the distribution are x/y axis aligned.
  public boolean isOutOfBounds(float numberSigmas) {
    float boundPointLeft = (float) (pos[0] - numberSigmas * Math.sqrt(covariance[0][0]));
    float boundPointRight = (float) (pos[0] + numberSigmas * Math.sqrt(covariance[0][0]));
    float boundPointUp = (float) (pos[1] - numberSigmas * Math.sqrt(covariance[1][1]));
    float boundPointDown = (float) (pos[1] + numberSigmas * Math.sqrt(covariance[1][1]));
    if (boundPointLeft < 0 || boundPointRight > (WIDTH - 1) || boundPointUp < 0 || boundPointDown > HEIGHT) {
      return true;
    } else {
      return false;
    }
  }

  public void setLayerID(boolean layerID) {
    this.layerID = layerID;
  }

  public boolean getLayerID() {
    // is this displayed?
    return this.layerID;
  }

  public float getActivity() {
    return this.activity;
  }

  public float[] getPos() {
    return this.pos;
  }

  public float[] getInitPos() {
    return this.initPos;
  }
}
