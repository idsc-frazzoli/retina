// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** This class implements an algorithm for Gaussian blob tracking which is inspired by the paper:
 * "asynchronous event-based multikernel algorithm for high-speed visual features tracking". */
public class DavisBlobTracker {
  // camera parameters
  private static final int WIDTH = 240; // maybe import width/height from other file?
  private static final int HEIGHT = 180;
  // tracker initialization parameters
  private static final int initNumberOfBlobs = 24;
  private static final int numberRows = 6; // on how many rows are the blobs initially distributed
  private static final int initVariance = 250;
  // algorithm parameters
  private static final float aUp = 0.2f; // if activity is higher, blob is in active layer
  private static final float aDown = 0.1f; // if activity is lower, active blob gets deleted
  private static final float scoreThreshold = 4e-4f; // score threshold for active blobs
  private static final float alphaOne = 0.9f; // for blob position update
  private static final float alphaTwo = 0.998f; // for blob covariance update
  private static final float alphaAttr = 0.002f; // attraction parameter - large value pulls blobs more towards initPos
  private static final float dAttr = 50; // [pixel] hidden blobs attraction
  private static final float dMerge = 20; // [pixel] if blobs closer than that, they merge
  private static final int boundaryDistance = 1; // [pixel] for out of bounds calculation
  private static final int tau = 8000; // [us] tunes activity update
  // fields
  private BlobFeatureFilter blobFeatureFilter; // next module in pipeline
  private final List<DavisSingleBlob> blobs;
  private int matchingBlob;
  private int lastEventTimestamp;
  // testing
  public float hitthreshold = 0;

  // initialize the tracker with all blobs uniformly distributed
  DavisBlobTracker(BlobFeatureFilter blobFeatureFilter) {
    blobs = new ArrayList<>(initNumberOfBlobs);
    int columnSpacing = WIDTH / numberRows;
    int rowSpacing = HEIGHT / (initNumberOfBlobs / numberRows);
    for (int i = 0; i < initNumberOfBlobs; i++) {
      int column = (i % numberRows);
      int row = i / numberRows; // use integer division
      DavisSingleBlob davisSingleBlob = new DavisSingleBlob((0.5f + column) * columnSpacing, (0.5f + row) * rowSpacing, initVariance);
      blobs.add(davisSingleBlob);
    }
    // this object is shared hence given to the constructor
    this.blobFeatureFilter = blobFeatureFilter;
  }

  // general todo list
  // TODO instead of exponential, use a lookup table or an approximation
  // TODO attraction equation: calculate on an evenbasis or time interval basis?
  // TODO implement merging operation and test it --> implemented
  // TODO generalize algorithm by testing several scoring functions and compare them
  public void receiveNewEvent(DavisDvsEvent davisDvsEvent) {
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
    // send active tracked blobs to feature filter
    blobFeatureFilter.receiveBlobList(getBlobList(1));
    // update time
    setEventTimestamp(davisDvsEvent.time);
  }

  private void calcScoreAndParams(DavisDvsEvent davisDvsEvent) {
    float highScore = 0;
    float hiddenHighScore = 0;
    int highScoreBlob = 0;
    int hiddenHighScoreBlob = 0;
    // calculate score for all blobs
    for (int i = 0; i < blobs.size(); i++) {
      // if (blobs.get(i).getLayerID()) {
      float score = blobs.get(i).gaussianBlobScore(davisDvsEvent);
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
    if (blobs.get(matchingBlob).getLayerID()) {
      hitthreshold++;
    }
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
    for (int i = 0; i < blobs.size(); i++) {
      if (i != matchingBlob) {
        blobs.get(i).updateBlobActivity(false, aUp, exponential);
      }
    }
    return isPromoted;
  }

  // if blob is promoted, we add a new hidden blob at its initial position.
  private void upgradeBlob() {
    // put element at the end of the list and promote it to active layer
    blobs.add(blobs.get(matchingBlob));
    blobs.get(blobs.size() - 1).setLayerID(true);
    // replace the promoted blob with a new initial blob
    float[] oldInitPos = blobs.get(matchingBlob).getInitPos();
    DavisSingleBlob newInitBlob = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
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
        DavisSingleBlob newInitBlob = new DavisSingleBlob(blobs.get(matchingBlob).getInitPos()[0], blobs.get(matchingBlob).getInitPos()[1], initVariance);
        blobs.set(matchingBlob, newInitBlob);
      }
    }
  }

  // delete active blobs when activity is lower than aDown
  private void deleteBlobs() {
    Iterator<DavisSingleBlob> iterator = blobs.iterator();
    while (iterator.hasNext()) {
      DavisSingleBlob davisSingleBlob = iterator.next();
      if (davisSingleBlob.getLayerID()) {
        if (davisSingleBlob.getActivity() < aDown) {
          iterator.remove();
        }
      }
    }
  }

  // apply attraction equation
  private void hiddenBlobAttraction() {
    boolean isReset;
    for (int i = 0; i < blobs.size(); i++) {
      if (!blobs.get(i).getLayerID()) {
        isReset = blobs.get(i).updateAttractionEquation(alphaAttr, dAttr);
      }
    }
  }

  // merge closest pair of active blobs if distance is less than dMerge
  private void mergeBlobs(float dMerge) {
    float minDistance = dMerge;
    int firstBlob = 0; // no active blob at 0 so its safe to assign 0
    int secondBlob = 0;
    // find pair of active blobs that is closest to each other
    for (int i = initNumberOfBlobs; i < (blobs.size() - 1); i++) {
      for (int j = i + 1; j < blobs.size(); j++) {
        float distance = blobs.get(i).getDistanceTo(blobs.get(j));
        if (distance < minDistance) {
          firstBlob = i;
          secondBlob = j;
          minDistance = distance;
        }
      }
    }
    // if blobs are closer than dMerge, one blob eats the other
    if (minDistance < dMerge) {
      blobs.get(firstBlob).eat(blobs.get(secondBlob));
      blobs.remove(secondBlob);
    }
  }

  public void printStatusUpdate(DavisDvsEvent davisDvsEvent) {
    if (matchingBlob >= blobs.size()) {
      System.out.println("Matching blob was deleted");
    } else {
      // number and activities of active blobs
      System.out.println(blobs.size() + " blobs, with " + getNumberOfBlobs(1) + " being in active layer.");
      for (int i = 0; i < blobs.size(); i++) {
        System.out.println("Blob #" + i + " with pos " + blobs.get(i).getPos()[0] + "/" + blobs.get(i).getPos()[1] + " and ID " + blobs.get(i).getLayerID());
      }
      // System.out.println(blobs.get(matchingBlob).getActivity() + " activity of matching blob # " + matchingBlob);
      // System.out.println(blobs.get(matchingBlob).getScore() + " score of matching blob");
      // System.out.printf("Updated blob position: %.2f/%.2f\n", blobs.get(matchingBlob).getPos()[0], blobs.get(matchingBlob).getPos()[1]);
      // System.out.println("Event params (x/y/p): " + davisDvsEvent.x + "/" + davisDvsEvent.y + "/" + davisDvsEvent.i);
      // System.out.println("Current timestamp: " + davisDvsEvent.time);
      // System.out.println("*********************************************************************");
    }
  }

  // return list of blobs for visualization and feature filtering
  // layerId=0: hidden blobs, layerId=1: active blobs
  public List<TrackedBlob> getBlobList(int layerId) {
    List<TrackedBlob> blobList = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      if (layerId == 1 && blobs.get(i).getLayerID()) {
        TrackedBlob trackedBlob = new TrackedBlob(blobs.get(i).getPos(), blobs.get(i).getCovariance(), getEventTimestamp(), false);
        blobList.add(trackedBlob);
      }
      if (layerId == 0 && !blobs.get(i).getLayerID()) {
        TrackedBlob trackedBlob = new TrackedBlob(blobs.get(i).getPos(), blobs.get(i).getCovariance(), getEventTimestamp(), true);
        blobList.add(trackedBlob);
      }
    }
    return blobList;
  }

  // return number of blobs. layerId=0: hidden blobs, layerId=1: active blobs, layerId=2: all blobs
  public int getNumberOfBlobs(int layerId) {
    int quantity = 0;
    if (layerId == 2) {
      return blobs.size();
    }
    for (int i = 0; i < blobs.size(); i++) {
      if (layerId == 1 && blobs.get(i).getLayerID()) {
        quantity++;
      }
      if (layerId == 0 && !blobs.get(i).getLayerID()) {
        quantity++;
      }
    }
    return quantity;
  }

  private void setEventTimestamp(int timestamp) {
    lastEventTimestamp = timestamp;
  }

  private int getEventTimestamp() {
    return lastEventTimestamp;
  }
}
