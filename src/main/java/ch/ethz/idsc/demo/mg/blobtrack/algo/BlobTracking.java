// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackObj;
import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** This class implements an algorithm for Gaussian blob tracking which is inspired by the paper:
 * "asynchronous event-based multikernel algorithm for high-speed visual features tracking".
 * BlobTrackObj objects are used internally by the tracking algorithm. For further processing, ImageBlob objects are used. */
public class BlobTracking {
  // camera parameters
  private final int width;
  private final int height;
  // tracker initialization parameters
  private final int initNumberOfBlobs;
  private final int numberRows; // on how many rows are the blobs initially distributed
  private final int initVariance;
  private final int defaultBlobID;
  // algorithm parameters
  private final float aUp; // if activity is higher, blob is in active layer
  private final float aDown; // if activity is lower, active blob gets deleted
  // private final float scoreThreshold; // score threshold for active blobs
  private final float alphaOne; // for blob position update
  private final float alphaTwo; // for blob covariance update
  private final float alphaAttr; // attraction parameter - large value pulls blobs more towards initPos
  private final float dAttr; // [pixel] hidden blobs attraction
  private final float dMerge; // [pixel] if blobs closer than that, they merge
  private final int boundaryDistance; // [pixel] for out of bounds calculation
  private final int tau; // [us] tunes activity update
  // fields
  private final List<BlobTrackObj> blobs;
  private int matchingBlob;
  private int lastEventTimestamp;
  // ID
  private int IDCount = 1;
  // testing
  public float hitthreshold = 0;

  BlobTracking(BlobTrackConfig pipelineConfig) {
    width = pipelineConfig.davisConfig.width.number().intValue();
    height = pipelineConfig.davisConfig.height.number().intValue();
    initNumberOfBlobs = pipelineConfig.initNumberOfBlobs.number().intValue();
    numberRows = pipelineConfig.numberRows.number().intValue();
    initVariance = pipelineConfig.initVariance.number().intValue();
    defaultBlobID = pipelineConfig.defaultBlobID.number().intValue();
    aUp = pipelineConfig.aUp.number().floatValue();
    aDown = pipelineConfig.aDown.number().floatValue();
    // scoreThreshold = pipelineConfig.scoreThreshold.number().floatValue();
    alphaOne = pipelineConfig.alphaOne.number().floatValue();
    alphaTwo = pipelineConfig.alphaTwo.number().floatValue();
    alphaAttr = pipelineConfig.alphaAttr.number().floatValue();
    dAttr = pipelineConfig.dAttr.number().floatValue();
    dMerge = pipelineConfig.dMerge.number().floatValue();
    boundaryDistance = pipelineConfig.boundaryDistance.number().intValue();
    tau = pipelineConfig.tau.number().intValue();
    // set static parameters for blob objects
    BlobTrackObj.setParams(pipelineConfig);
    // initialize the tracker with all blobs uniformly distributed
    blobs = new ArrayList<>(initNumberOfBlobs);
    int columnSpacing = width / numberRows;
    int rowSpacing = height / (initNumberOfBlobs / numberRows);
    for (int i = 0; i < initNumberOfBlobs; i++) {
      int column = (i % numberRows);
      int row = i / numberRows; // use integer division
      BlobTrackObj blobTrackObj = new BlobTrackObj((0.5f + column) * columnSpacing, (0.5f + row) * rowSpacing, initVariance);
      blobs.add(blobTrackObj);
    }
  }

  // general todo list
  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    // associate the event with matching blob
    calcScoreAndParams(davisDvsEvent);
    // update activity of all blobs and check if matching blob gets promoted
    boolean isPromoted = updateActivity(davisDvsEvent);
    // promote blob and put new hidden blob into hidden layer
    if (isPromoted) {
      upgradeBlob();
    }
    // out of bound check: only updated blob needs to be checked
    outOfBoundsCheck();
    // delete active blobs when threshold falls below aDown (hysteresis style)
    deleteBlobs();
    // attraction equation for hidden blobs
    hiddenBlobAttraction();
    // for testing
    // printStatusUpdate(davisDvsEvent);
    // merging operation
    mergeBlobs(dMerge);
    // update time
    setEventTimestamp(davisDvsEvent.time);
  }

  private void calcScoreAndParams(DavisDvsEvent davisDvsEvent) {
    float highScore = 0;
    // float hiddenHighScore = 0;
    int highScoreBlob = 0;
    // int hiddenHighScoreBlob = 0;
    // calculate score for all blobs
    for (int i = 0; i < blobs.size(); ++i) {
      // if (blobs.get(i).getLayerID()) {
      float score = (float) GaussianBlobScore.INSTANCE.evaluate(blobs.get(i), davisDvsEvent);
      blobs.get(i).setCurrentScore(score);
      // store highest score and which blob it belongs to
      if (score > highScore) {
        highScore = score;
        highScoreBlob = i;
      }
      // }
    }
    // if one active blob hits threshold, update it
    // if (highScore > scoreThreshold) {
    blobs.get(highScoreBlob).updateBlobParameters(davisDvsEvent, alphaOne, alphaTwo);
    matchingBlob = highScoreBlob;
    if (blobs.get(matchingBlob).getLayerID())
      ++hitthreshold;
    // } else {
    // for (int i = 0; i < blobs.size(); i++) {
    // if (!blobs.get(i).getLayerID()) {
    // float hiddenScore = blobs.get(i).gaussianBlobScore(davisDvsEvent);
    // store highest score and which blob it belongs to
    // if (hiddenScore > hiddenHighScore) {
    // hiddenHighScore = hiddenScore;
    // hiddenHighScoreBlob = i;
    // }
    // }
    // }
    // blobs.get(hiddenHighScoreBlob).updateBlobParameters(davisDvsEvent, alphaOne, alphaTwo);
    // matchingBlob = hiddenHighScoreBlob;
    // }
  }

  // update blob activity. If matching blob is in hidden layer and activity hits aUp, return true
  private boolean updateActivity(DavisDvsEvent davisDvsEvent) {
    boolean isPromoted;
    float deltaT = davisDvsEvent.time - getEventTimestamp(); // in [us]
    float exponent = deltaT / tau;
    float exponential = (float) Math.exp(-exponent);
    isPromoted = blobs.get(matchingBlob).updateBlobActivity(true, aUp, exponential);
    for (int i = 0; i < blobs.size(); ++i)
      if (i != matchingBlob)
        blobs.get(i).updateBlobActivity(false, aUp, exponential);
    return isPromoted;
  }

  // if blob is promoted, we add a new hidden blob at its initial position.
  private void upgradeBlob() {
    // put element at the end of the list and promote it to active layer
    blobs.add(blobs.get(matchingBlob));
    blobs.get(blobs.size() - 1).setToActiveLayer(IDCount);
    IDCount++;
    // replace the promoted blob with a new initial blob
    float[] oldInitPos = blobs.get(matchingBlob).getInitPos();
    BlobTrackObj newInitBlob = new BlobTrackObj(oldInitPos[0], oldInitPos[1], initVariance);
    blobs.set(matchingBlob, newInitBlob);
    // set new reference since we moved matchingBlob to end of list
    matchingBlob = blobs.size() - 1;
  }

  // delete/restore matching blob if it moved out of bounds
  private void outOfBoundsCheck() {
    if (blobs.get(matchingBlob).isOutOfBounds(boundaryDistance)) {
      if (blobs.get(matchingBlob).getLayerID()) {
        // active blobs get deleted
        blobs.remove(matchingBlob);
      } else {
        // hidden blobs get re-initialized
        BlobTrackObj newInitBlob = new BlobTrackObj(blobs.get(matchingBlob).getInitPos()[0], blobs.get(matchingBlob).getInitPos()[1], initVariance);
        blobs.set(matchingBlob, newInitBlob);
      }
    }
  }

  // delete active blobs when activity is lower than aDown
  private void deleteBlobs() {
    for (Iterator<BlobTrackObj> iterator = blobs.iterator(); iterator.hasNext();) {
      BlobTrackObj davisSingleBlob = iterator.next();
      if (davisSingleBlob.getLayerID())
        if (davisSingleBlob.getActivity() < aDown)
          iterator.remove();
    }
  }

  // apply attraction equation
  private void hiddenBlobAttraction() {
    for (int i = 0; i < blobs.size(); ++i)
      if (!blobs.get(i).getLayerID())
        blobs.get(i).updateAttractionEquation(alphaAttr, dAttr);
  }

  // merge closest pair of active blobs if distance is less than dMerge
  private void mergeBlobs(float dMerge) {
    double minDistance = dMerge;
    int firstBlob = 0; // no active blob at 0 so its safe to assign 0
    int secondBlob = 0;
    // find pair of active blobs that is closest to each other
    for (int i = initNumberOfBlobs; i < blobs.size() - 1; ++i)
      for (int j = i + 1; j < blobs.size(); ++j) {
        double distance = blobs.get(i).getDistanceTo(blobs.get(j).getPos());
        if (distance < minDistance) {
          firstBlob = i;
          secondBlob = j;
          minDistance = distance;
        }
      }
    // if blobs are closer than dMerge, one blob eats the other
    if (minDistance < dMerge) {
      blobs.get(firstBlob).eat(blobs.get(secondBlob));
      blobs.remove(secondBlob);
    }
  }

  public void printStatusUpdate(DavisDvsEvent davisDvsEvent) {
    if (matchingBlob >= blobs.size())
      System.out.println("Matching blob was deleted");
    else {
      // number and activities of active blobs
      System.out.println(blobs.size() + " blobs, with " + getNumberOfActiveBlobs() + " being in active layer.");
      for (int i = 0; i < blobs.size(); ++i)
        System.out.println("Blob #" + i + " with pos " + blobs.get(i).getPos()[0] + "/" + blobs.get(i).getPos()[1] + " and ID " + blobs.get(i).getLayerID());
    }
  }

  // TODO MG helper function to create ImageBlob from BlobTrackObj
  public List<ImageBlob> getActiveBlobs() {
    List<ImageBlob> list = new ArrayList<>();
    for (int i = 0; i < blobs.size(); ++i)
      if (blobs.get(i).getLayerID()) {
        ImageBlob activeBlob = new ImageBlob( //
            blobs.get(i).getPos(), //
            blobs.get(i).getCovariance(), //
            getEventTimestamp(), //
            false, //
            blobs.get(i).getBlobID());
        list.add(activeBlob);
      }
    return list;
  }

  public List<ImageBlob> getHiddenBlobs() {
    List<ImageBlob> list = new ArrayList<>();
    for (int i = 0; i < blobs.size(); ++i)
      if (!blobs.get(i).getLayerID()) {
        ImageBlob hiddenBlob = new ImageBlob( //
            blobs.get(i).getPos(), //
            blobs.get(i).getCovariance(), //
            getEventTimestamp(), //
            true, //
            defaultBlobID);
        list.add(hiddenBlob);
      }
    return list;
  }

  private int getNumberOfActiveBlobs() {
    int numberOfActiveBlobs = 0;
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getLayerID())
        ++numberOfActiveBlobs;
    }
    return numberOfActiveBlobs;
  }

  private void setEventTimestamp(int timestamp) {
    lastEventTimestamp = timestamp;
  }

  private int getEventTimestamp() {
    return lastEventTimestamp;
  }
}
