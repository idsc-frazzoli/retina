//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class DavisSingleBlob {
  private static final int WIDTH = 240; // maybe import those values from other file?
  private static final int HEIGHT = 180;
  private static int lastTimestamp = 0; // [us]
  private final float[] initPos; // float to avoid type cast all the time
  private float[] pos;
  private float[][] covariance;
  private float activity;
  private int layerID; // = 0 for active layer, = 1 for hidden layer
  private float currentScore;

  // initialize with position and covariance
  DavisSingleBlob(float initialX, float initialY, float initSigma) {
    initPos = new float[] { initialX, initialY };
    pos = initPos;
    covariance = new float[][] { { initSigma, 0 }, { 0, initSigma } };
    layerID = 1;
  }

  // stores the timestamp of last event
  public static void updateTimestamp(DavisDvsEvent davisDvsEvent) {
    lastTimestamp = davisDvsEvent.time;
  }

  // updates the activity of a blob
  public void updateBlobActivity(DavisDvsEvent davisDvsEvent, double tau, boolean hasHighestScore) {
    double deltaT = davisDvsEvent.time - lastTimestamp;
    double exponent = deltaT / tau;
    float decreaseTerm = (float) Math.exp(-exponent);
    if (hasHighestScore) {
      this.activity = decreaseTerm + currentScore;
    } else {
      this.activity = decreaseTerm;
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
    covariance[0][0] = (1 - alphaTwo) * covariance[0][0] + alphaTwo * deltaCovariance[0][0];
    covariance[0][1] = (1 - alphaTwo) * covariance[0][1] + alphaTwo * deltaCovariance[0][1];
    covariance[1][0] = (1 - alphaTwo) * covariance[1][0] + alphaTwo * deltaCovariance[1][0];
    covariance[1][1] = (1 - alphaTwo) * covariance[1][1] + alphaTwo * deltaCovariance[1][1];
    // TODO check here covariance is not too small since we only track blobs above a certain size
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
    currentScore = (float) (Math.exp(exponent)); // no normalization
    return currentScore;
  }

  public void updateAttractionEquation(float alphaAttr, int dMax) {
    float positionDiff = (float) Math.sqrt((pos[0] - initPos[0]) * (pos[0] - initPos[0]) + (pos[1] - initPos[1]) * (pos[1] - initPos[1]));
    if (positionDiff > dMax) {
      pos = initPos;
    } else {
      pos[0] = pos[0] + alphaAttr * (initPos[0] - pos[0]);
      pos[1] = pos[1] + alphaAttr * (initPos[1] - pos[1]);
    }
  }

  // perform the layer logic. Return true if blob is promoted to active layer.
  public boolean blobPromotion(float aUp) {
    if (activity > aUp) {
      layerID = 0;
      return true;
    } else {
      layerID = 1;
      return false;
    }
  }

  // checks if the blob is too close to a boarder and if so returns true.
  // we assume that the axes of the distribution are x/y axis aligned.
  public boolean isOutOfBounds() {
    float boundPointLeft = (float) (pos[0] - Math.sqrt(covariance[0][0]));
    float boundPointRight = (float) (pos[0] + Math.sqrt(covariance[0][0]));
    float boundPointUp = (float) (pos[1] - Math.sqrt(covariance[1][1]));
    float boundPointDown = (float) (pos[1] + Math.sqrt(covariance[1][1]));
    if (boundPointLeft < 0 || boundPointRight > (WIDTH - 1) || boundPointUp < 0 || boundPointDown > (HEIGHT)) {
      return true;
    } else {
      return false;
    }
  }

  // update position with repulsion equation
  void updateRepulsionEquation(DavisDvsEvent davisDvsEvent) {
  }

  // sets layerID
  public void setLayerID(int layerID) {
    this.layerID = layerID;
  }

  // returns layerID
  public int getLayerID() {
    return this.layerID;
  }

  // returns activity
  public float getActivity() {
    return this.activity;
  }

  // returns initial position
  public float[] getInitPos() {
    return this.initPos;
  }
}
