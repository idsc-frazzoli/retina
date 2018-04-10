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
  private static final int initNumberOfBlobs = 35;
  private static final int numberRows = 7; // on how many rows are the blobs initially distributed
  private static final int initVariance = 250;
  // algorithm parameters
  private static final float aUp = 0.15f; // if activity is higher, blob is in active layer
  private static final float aDown = 0.05f; // if activity is lower, active blob gets deleted
  private static final float scoreThreshold = 2e-4f; // score threshold for active blobs
  private static final float alphaOne = 0.9f; // for blob position update
  private static final float alphaTwo = 0.995f; // for blob covariance update
  private static final float alphaAttr = 0.002f; // attraction parameter - large value pulls blobs more towards initPos
  private static final float dAttr = 70; // [pixel] hidden blobs attraction
  private static final float enlargementFactor = 1.01f; // increase covariance matrix by this if too small
  private static final float minSize = 150; // trace of matrix should not be smaller than this
  // private final float alphaRep = 0; // repulsion equation
  // private final float dRep = 10; // repulsion equation
  private static final int boundaryDistance = 1; // [pixel] for out of bounds calculation
  private static final int tau = 1000; // [us] tunes activity update
  // private final int acquiringInterval = 2500; // [us] acquiring interval to rise activity over aDown
  private static final int attractInterval = 3; // [us] update interval attraction
  // fields
  private final List<DavisSingleBlob> blobs;
  private int matchingBlob;
  private int lastEventTimestamp;
  private int lastUpdateTimestamp;
  private int lastAcquireTimestamp;
  // testing
  public float hitthreshold = 0;

  // initialize the tracker with all blobs uniformly distributed
  DavisBlobTracker() {
    blobs = new ArrayList<>();
    int columnSpacing = WIDTH / numberRows;
    int rowSpacing = HEIGHT / (initNumberOfBlobs / numberRows);
    for (int i = 0; i < initNumberOfBlobs; i++) {
      int column = (i % numberRows);
      int row = i / numberRows; // use integer division
      DavisSingleBlob davisSingleBlob = new DavisSingleBlob((0.5f + column) * columnSpacing, (0.5f + row) * rowSpacing, initVariance);
      blobs.add(davisSingleBlob);
    }
  }

  // general todo list
  // TODO when searching for active blobs, can skip first initialNumberOfBlobs (they are always hidden)
  // TODO instead of exponential, use a lookup table or an approximation
  // TODO attraction equation: calculate on an evenbasis or time interval basis?
  // TODO why is there a score threshold for active blobs --> is removed, investigate further
  // TODO implement merging operation and test it
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
    // restore/delete blobs if activity not high enough after acquiringInterval
    // if ((davisDvsEvent.time - getAcquireTimestamp()) > acquiringInterval) {
    // deletelowActivityBlobs();
    // setAcquireTimestamp(davisDvsEvent.time);
    // }
    hiddenBlobAttraction();
    // control the blob size
    controlBlobSize();
    if (davisDvsEvent.time - getUpdateTimestamp() > attractInterval) {
      setUpdateTimestamp(davisDvsEvent.time);
    }
    // // printStatusUpdate(davisDvsEvent);
    // }
    // update time
    setEventTimestamp(davisDvsEvent.time);
  }

  private void calcScoreAndParams(DavisDvsEvent davisDvsEvent) {
    float highScore = 0;
    float hiddenHighScore = 0;
    int highScoreBlob = 0;
    int hiddenHighScoreBlob = 0;
    // calculate score for all active blobs
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
    //// } else {
    // for (int i = 0; i < blobs.size(); i++) {
    // if (!blobs.get(i).getLayerID()) {
    // float hiddenScore = blobs.get(i).gaussianBlobScore(davisDvsEvent);
    // // store highest score and which blob it belongs to
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
    // sanity check: no active blob should be promoted
    if (blobs.get(matchingBlob).getLayerID() && isPromoted) {
      System.out.println("Active blob is being promoted. This should not happen!");
    }
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

  // degrade active blobs with activity lower than aUp to hidden layer
  private void deleteBlobs() {
    // FIXME this is probably not functioning
    // for (int i = 0; i < blobs.size(); i++) {
    // if (blobs.get(i).getLayerID()) {
    // if (blobs.get(i).getActivity() < aDown) {
    // blobs.remove(i);
    // }
    // }
    // }
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
  // // delete/restore blobs with low activity
  // private void deletelowActivityBlobs() {
  // // initial blobs with low activity are put back to initial position
  // for (int i = 0; i < initNumberOfBlobs; i++) {
  // if (blobs.get(i).getActivity() < aDown) {
  // DavisSingleBlob newInitBlob = new DavisSingleBlob(blobs.get(i).getInitPos()[0], blobs.get(i).getInitPos()[1], initVariance);
  // blobs.set(i, newInitBlob);
  // }
  // }
  // // make sure further blobs are present
  // if (blobs.size() == initNumberOfBlobs) {
  // return;
  // }
  // // delete all additional blobs with low activity
  // for (int i = initNumberOfBlobs; i < blobs.size(); i++) {
  // if (blobs.get(i).getActivity() < aDown) {
  // // System.out.println("Blob #" + i + " of " + blobs.size() + " has been removed due to low activity!");
  // blobs.remove(i);
  // }
  // }
  // }

  // apply attraction equation
  private void hiddenBlobAttraction() {
    boolean isReset;
    for (int i = 0; i < blobs.size(); i++) {
      if (!blobs.get(i).getLayerID()) {
        isReset = blobs.get(i).updateAttractionEquation(alphaAttr, dAttr);
        if (isReset) {
          // System.out.println("Blob # " + i + " is reset due to attraction equation");
        }
      }
    }
  }

  // control blob size. Size metric: sum of eigenvalues. TODO maybe different for active and hidden blobs?
  private void controlBlobSize() {
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getSizeMetric() < minSize) {
        blobs.get(i).increaseBlobSize(enlargementFactor);
      }
    }
  }

  //
  // // apply repulsion equation
  // private void calcRepulsion() {
  // // find each pair of two trackers
  // for (int i = 0; i < (blobs.size() - 1); i++) {
  // for (int j = i; j < blobs.size(); j++) {
  // blobs.get(i).updateRepulsionEquation(alphaRep, dRep, blobs.get(j));
  // }
  // }
  // }
  //
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

  // return list of blobs. layerId=0: hidden blobs, layerId=1: active blobs, layerId=2: all blobs
  public List<DavisSingleBlob> getBlobList(int layerId) {
    List<DavisSingleBlob> blobList = new ArrayList<>();
    if (layerId == 2) {
      return blobs;
    }
    for (int i = 0; i < blobs.size(); i++) {
      if (layerId == 1 && blobs.get(i).getLayerID()) {
        blobList.add(blobs.get(i));
      }
      if (layerId == 0 && !blobs.get(i).getLayerID()) {
        blobList.add(blobs.get(i));
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

  private void setUpdateTimestamp(int timestamp) {
    lastUpdateTimestamp = timestamp;
  }

  private int getUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  private void setAcquireTimestamp(int timestamp) {
    lastAcquireTimestamp = timestamp;
  }

  private int getAcquireTimestamp() {
    return lastAcquireTimestamp;
  }
}
