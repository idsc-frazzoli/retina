// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** provides blob object for the tracking algorithm */
public class BlobTrackObj {
  // camera parameters
  private static int WIDTH;
  private static int HEIGHT;
  private static int DEFAULT_BLOB_ID;

  /** set static parameters of class
   * 
   * @param blobTrackConfig */
  public static void setParams(BlobTrackConfig blobTrackConfig) {
    WIDTH = blobTrackConfig.davisConfig.width.number().intValue();
    HEIGHT = blobTrackConfig.davisConfig.height.number().intValue();
    DEFAULT_BLOB_ID = blobTrackConfig.defaultBlobID.number().intValue();
  }

  // ---
  // blob parameters
  private final double[][] covariance;
  private final float[] initPos;
  private final float[] pos;
  private boolean layerID; // true for active layer, false for hidden layer
  private float currentScore;
  private float activity;
  private int blobID;

  // initialize with position and covariance
  public BlobTrackObj(float initialX, float initialY, float initVariance) {
    initPos = new float[] { initialX, initialY };
    pos = new float[] { initialX, initialY };
    covariance = new double[][] { //
        { initVariance, 0 }, //
        { 0, initVariance } };
    layerID = false;
    currentScore = 0;
    activity = 0;
    blobID = DEFAULT_BLOB_ID;
  }

  /** updates the activity of a blob
   * 
   * @param hasHighestScore
   * @param aUp
   * @param exponential
   * @return */
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

  /** updates parameters of matching blob
   * 
   * @param davisDvsEvent
   * @param alphaOne
   * @param alphaTwo */
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
    float[][] deltaCovariance = { //
        { deltaXX, deltaXY }, //
        { deltaXY, deltaYY } };
    // covariance update
    covariance[0][0] = alphaTwo * covariance[0][0] + (1 - alphaTwo) * deltaCovariance[0][0];
    covariance[0][1] = alphaTwo * covariance[0][1] + (1 - alphaTwo) * deltaCovariance[0][1];
    covariance[1][0] = alphaTwo * covariance[1][0] + (1 - alphaTwo) * deltaCovariance[1][0];
    covariance[1][1] = alphaTwo * covariance[1][1] + (1 - alphaTwo) * deltaCovariance[1][1];
  }

  // public float gaborBlobScore(DavisDvsEvent davisDvsEvent) {
  // double sigma = 3;
  // double gamma = sigma / 15;
  // double lambda = 4 * sigma;
  // double theta = Math.PI / 2;
  // double cos_t = Math.cos(theta);
  // double sin_t = Math.sin(theta);
  // double xU = +(davisDvsEvent.x - pos[0]) * cos_t + (davisDvsEvent.y - pos[1]) * sin_t;
  // double yU = -(davisDvsEvent.x - pos[0]) * sin_t + (davisDvsEvent.y - pos[1]) * cos_t;
  // currentScore = (float) Math.exp((xU * xU + gamma * gamma * yU * yU) / (2 * sigma * sigma) * Math.cos(2 * Math.PI * xU / lambda));
  // return currentScore = (float) GaborBlobScore.INSTANCE.evaluate(this, davisDvsEvent);
  // }
  // // maybe use also Manhattan distance?
  // public double geometricBlobScore(DavisDvsEvent davisDvsEvent) {
  // double distance = Math.sqrt((davisDvsEvent.x - pos[0]) * (davisDvsEvent.x - pos[0]) + (davisDvsEvent.y - pos[1]) * (davisDvsEvent.y - pos[1]));
  // // somehow normalize the distance
  // return distance;
  // }
  public void updateAttractionEquation(float alphaAttr, float dRep) {
    double posDiff = getDistanceTo(initPos);
    if (dRep < posDiff) {
      pos[0] = initPos[0];
      pos[1] = initPos[1];
    } else {
      pos[0] += alphaAttr * (initPos[0] - pos[0]);
      pos[1] += alphaAttr * (initPos[1] - pos[1]);
    }
  }

  /** required for merging
   * 
   * @param otherPos
   * @return */
  public double getDistanceTo(float[] otherPos) {
    return Math.hypot(pos[0] - otherPos[0], pos[1] - otherPos[1]);
  }

  /** merge blobs by using activity-weighted average
   * 
   * @param otherBlob */
  public void eat(BlobTrackObj otherBlob) {
    final float totActivity = activity + otherBlob.getActivity();
    // position merge
    pos[0] = (activity * pos[0] + otherBlob.getActivity() * otherBlob.getPos()[0]) / totActivity;
    pos[1] = (activity * pos[1] + otherBlob.getActivity() * otherBlob.getPos()[1]) / totActivity;
    // covariance merge TODO find out which is the correct way to do that
    covariance[0][0] = 0.5 * (covariance[0][0] + otherBlob.getCovariance()[0][0]);
    covariance[0][1] = 0.5 * (covariance[0][1] + otherBlob.getCovariance()[0][1]);
    covariance[1][0] = 0.5 * (covariance[1][0] + otherBlob.getCovariance()[1][0]);
    covariance[1][1] = 0.5 * (covariance[1][1] + otherBlob.getCovariance()[1][1]);
    activity = totActivity;
  }

  // if blob is too close to boundary, return true
  public boolean isOutOfBounds(int boundaryDistance) {
    float boundPointLeft = pos[0] - boundaryDistance;
    float boundPointRight = pos[0] + boundaryDistance;
    float boundPointUp = pos[1] - boundaryDistance;
    float boundPointDown = pos[1] + boundaryDistance;
    return boundPointLeft < 0 //
        || boundPointRight > WIDTH - 1 //
        || boundPointUp < 0 //
        || boundPointDown > HEIGHT;
  }

  public void setToActiveLayer(int blobID) {
    this.blobID = blobID;
    this.layerID = true;
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

  public int getBlobID() {
    return blobID;
  }

  public void setCurrentScore(float score) {
    currentScore = score;
  }
}
