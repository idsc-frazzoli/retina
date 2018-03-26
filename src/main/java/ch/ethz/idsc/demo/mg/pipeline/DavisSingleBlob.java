//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class DavisSingleBlob {
  // camera parameters
  private static final int WIDTH = 240; // maybe import those values from other file?
  private static final int HEIGHT = 180;
  // static fields
  // TODO longterm: static field
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
    pos = new float[] { initialX, initialY };
    covariance = new float[][] { { initVariance, 0 }, { 0, initVariance } };
    layerID = false;
    activity = 0.0f;
    currentScore = 0.0f;
  }

  // stores the timestamp of last event
  public static void updateTimestamp(DavisDvsEvent davisDvsEvent) {
    lastTimestamp = davisDvsEvent.time;
  }

  public static int getTimestamp() {
    return lastTimestamp;
  }

  // updates the activity of a blob
  public boolean updateBlobActivity(DavisDvsEvent davisDvsEvent, int tau, boolean hasHighestScore, float aUp, float exponential) {
    boolean isPromoted;
    if (hasHighestScore) {
      // if hidden layer blob hits threshold it should be promoted
      isPromoted = !layerID && (this.activity * exponential + currentScore) > aUp;
      activity = activity * exponential + currentScore;
      return isPromoted;
    }
    activity = activity * exponential;
    isPromoted = false;
    return isPromoted;
  }

  // updates the blob with a new event that is associated with it
  public void updateBlobParameters(DavisDvsEvent davisDvsEvent, float alphaOne, float alphaTwo) {
    // cast into float
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
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
    float eventPosX = davisDvsEvent.x;
    float eventPosY = davisDvsEvent.y;
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
    currentScore = (float) (1 / (2 * Math.PI) * 1 / Math.sqrt(covarianceDeterminant) * Math.exp(exponent));
    // currentScore = (float) (Math.exp(exponent));
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
    System.out.println(otherPos[0]);
    float exponential = (float) (Math.exp(posDiff / dRep));
    float denominator = otherBlob.getActivity() * otherBlob.getActivity() + activity * activity;
    //TODO: make sure denominator is not zero!! this fucks up quite a lot of things
//    pos[0] = pos[0] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
//        / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[0] - pos[0]);
//    pos[1] = pos[1] - alphaRep * exponential * otherBlob.getActivity() * otherBlob.getActivity()
//        / (otherBlob.getActivity() * otherBlob.getActivity() + activity * activity) * (otherPos[1] - pos[1]);
  }

  public boolean blobPromotion(float aUp) {
    layerID = activity > aUp;
    return layerID;
  }

  // checks if the blob is too close to a boarder and if so returns true.
  // TODO we assume that the axes of the distribution are x/y axis aligned.
  public boolean isOutOfBounds(float numberSigmas) {
    float boundPointLeft = (float) (pos[0] - numberSigmas * Math.sqrt(covariance[0][0]));
    float boundPointRight = (float) (pos[0] + numberSigmas * Math.sqrt(covariance[0][0]));
    float boundPointUp = (float) (pos[1] - numberSigmas * Math.sqrt(covariance[1][1]));
    float boundPointDown = (float) (pos[1] + numberSigmas * Math.sqrt(covariance[1][1]));
    return boundPointLeft < 0 || boundPointRight > (WIDTH - 1) || boundPointUp < 0 || boundPointDown > HEIGHT;
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

  public float getScore() {
    return currentScore;
  }
}
