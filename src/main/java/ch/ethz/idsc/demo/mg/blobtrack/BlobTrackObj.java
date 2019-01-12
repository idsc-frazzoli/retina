// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** blob object for the tracking algorithm */
public class BlobTrackObj {
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

  private final double[][] covariance;
  private final float[] initPos;
  private final float[] pos;
  // ---
  private boolean layerID; // true for active layer, false for hidden layer
  private float currentScore;
  private float activity;
  private int blobID;

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
   * @param aUp score threshold for active layer
   * @param exponential activity update parameter
   * @return true if blob is promoted to active layer */
  public boolean updateBlobActivity(boolean hasHighestScore, float aUp, float exponential) {
    boolean isPromoted;
    if (hasHighestScore) {
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
    // covariance merge
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
    layerID = true;
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

  public void setCurrentScore(float currentScore) {
    this.currentScore = currentScore;
  }
}
