//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** This class implements an algorithm for Gaussian blob tracking as
 * described in the paper "asynchronous event-based multikernel
 * algorithm for high-speed visual features tracking. */
public class DavisBlobTracker {
  // camera parameters
  private static final int WIDTH = 240; // maybe import width/height from other file?
  private static final int HEIGHT = 180;
  // tracker initialization parameters
  private static final int initNumberOfBlobs = 24;
  private static final int numberRows = 6; // on how many rows are the blobs initially distributed
  private static final int initVariance = 1900;
  // algorithm parameters
  private final float aUp = 1e-3f; // if activity is higher, blob is in active layer
  private final float aDown = 1e-4f; // if activity is lower, blob gets deleted
  private final float scoreThreshold = 5e-4f; // score threshold for active blobs
  private final float alphaOne = 0.1f; // for blob position update
  private final float alphaTwo = 0.01f; // for blob covariance update
  private final float numberSigmas = 0.5f; // for out of bounds calculation
  private final float alphaAttr = 0.01f; // hidden blobs attraction
  private final float dAttr = 40; // [pixel] hidden blobs attraction
  private final float alphaRep = 0; // repulsion equation
  private final float dRep = 10; // repulsion equation
  private final int tau = 500; // [us] tunes activity update
  private final int updateInterval = 100; // [us] update interval for deletion, attraction, repulsion functions
  // fields
  List<DavisSingleBlob> blobs;
  private int matchingBlob;
  private int lastEventTimestamp;
  private int lastUpdateTimestamp;
  // just for the status update
  // private int lastStatusUpdate = 0; // [us]

  // initialize the tracker with all blobs uniformly distributed
  DavisBlobTracker() {
    blobs = new ArrayList<>();
    int rowSpacing = WIDTH / (numberRows + 1);
    int columnSpacing = HEIGHT / (initNumberOfBlobs / numberRows + 1);
    for (int i = 0; i < initNumberOfBlobs; i++) {
      int column = (i % numberRows) + 1;
      int row = i / numberRows + 1; // use integer division
      DavisSingleBlob davisSingleBlob = new DavisSingleBlob(column * columnSpacing, row * rowSpacing, initVariance);
      blobs.add(davisSingleBlob);
    }
  }

  public void receiveNewEvent(DavisDvsEvent davisDvsEvent) {
    // associate the event with matching blob
    calcScoreAndParams(davisDvsEvent);
    // update activity of all blobs and check if matching blob gets promoted
    boolean isPromoted = updateActivity(davisDvsEvent);
    // promote blob and put new hidden blob into hidden layer
    if (isPromoted) {
      upgradeBlob();
    }
    // degrade active blobs when activity is too low for active layer
    degradeBlobs();
    // this part is not required for every single event
    if ((getUpdateTimestamp() - davisDvsEvent.time) > updateInterval) {
      System.out.println("*****************Update is happening******************");
      // delete blobs if activity too low for hidden layer or out of bounds
      deleteBlobs();
      // repulse blobs from each other
      calcRepulsion();
      // for all blobs of hidden layer use attraction equation
      hiddenBlobAttraction();
      setUpdateTimestamp(davisDvsEvent.time);
    }
    printStatusUpdate(davisDvsEvent);
    // TODO check if blobs are too small/ wrong shape and delete not fitting blobs
    // TODO put processed tracked blobs in appropriate data structure and send it to next module
    setEventTimestamp(davisDvsEvent.time);
  }

  private void calcScoreAndParams(DavisDvsEvent davisDvsEvent) {
    float highScore = 0;
    float hiddenHighScore = 0;
    int highScoreBlob = 0;
    int hiddenHighScoreBlob = 0;
    // calculate score for all active blobs
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getLayerID()) {
        float individualScore = blobs.get(i).calculateBlobScore(davisDvsEvent);
        // store highest score and which blob it belongs to
        if (individualScore > highScore) {
          highScore = individualScore;
          highScoreBlob = i;
        }
      }
    }
    // if one active blob hits threshold, update it
    if (highScore > scoreThreshold) {
      blobs.get(highScoreBlob).updateBlobParameters(davisDvsEvent, alphaOne, alphaTwo);
      matchingBlob = highScoreBlob;
    } else {
      for (int i = 0; i < blobs.size(); i++) {
        if (!blobs.get(i).getLayerID()) {
          float hiddenScore = blobs.get(i).calculateBlobScore(davisDvsEvent);
          // store highest score and which blob it belongs to
          if (hiddenScore > hiddenHighScore) {
            hiddenHighScore = hiddenScore;
            hiddenHighScoreBlob = i;
          }
        }
      }
      blobs.get(hiddenHighScoreBlob).updateBlobParameters(davisDvsEvent, alphaOne, alphaTwo);
      matchingBlob = hiddenHighScoreBlob;
    }
  }

  // update blob activity. If matching blob is in hidden layer and activity hits aUp, return true
  private boolean updateActivity(DavisDvsEvent davisDvsEvent) {
    boolean isPromoted;
    float deltaT = (float) (davisDvsEvent.time - getEventTimestamp());
    float exponent = deltaT / tau;
    float exponential = (float) Math.exp(-exponent);
    isPromoted = blobs.get(matchingBlob).updateBlobActivity(davisDvsEvent, true, aUp, exponential);
    // sanity check: no active blob should be promoted
    if (blobs.get(matchingBlob).getLayerID() && isPromoted) {
      System.out.println("Active blob is being promoted. This should not happen!");
    }
    for (int i = 0; i < blobs.size(); i++) {
      if (i != matchingBlob) {
        blobs.get(i).updateBlobActivity(davisDvsEvent, false, aUp, exponential);
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
  private void degradeBlobs() {
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getLayerID()) {
        if (blobs.get(i).getActivity() < aUp) {
          blobs.get(i).setLayerID(false);
        }
      }
    }
  }

  // delete blobs with low activity or out of bounds
  private void deleteBlobs() {
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getActivity() < aDown || blobs.get(i).isOutOfBounds(numberSigmas)) {
        // if not enough hidden blobs, we put it back at initial position
        if (getNumberOfBlobs(false) <= initNumberOfBlobs) {
          float[] oldInitPos = blobs.get(i).getInitPos();
          DavisSingleBlob newInitBlob = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
          blobs.set(i, newInitBlob);
        } else {
          blobs.remove(i);
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
        if (isReset) {
          System.out.println("Blob # " + i + " is reset to initial position.");
        }
      }
    }
  }

  // apply repulsion equation
  private void calcRepulsion() {
    // find each pair of two trackers
    for (int i = 0; i < (blobs.size() - 1); i++) {
      for (int j = i; j < blobs.size(); j++) {
        blobs.get(i).updateRepulsionEquation(alphaRep, dRep, blobs.get(j));
        // System.out.printf("Updated blob position: %.2f/%.2f\n",blobs.get(i).getPos()[0], blobs.get(i).getPos()[1]);
      }
    }
  }

  private void printStatusUpdate(DavisDvsEvent davisDvsEvent) {
    if (matchingBlob >= blobs.size()) {
      System.out.println("Matching blob was deleted");
    } else {
      // number and activities of active blobs
      System.out.println(blobs.size() + " blobs, with " + getNumberOfBlobs(true) + " being in active layer.");
      System.out.println(blobs.get(matchingBlob).getActivity() + " activity of matching blob # " + matchingBlob);
      System.out.println(blobs.get(matchingBlob).getScore() + " score of matching blob");
      System.out.printf("Updated blob position: %.2f/%.2f\n", blobs.get(matchingBlob).getPos()[0], blobs.get(matchingBlob).getPos()[1]);
      System.out.println("Event params (x/y/p): " + davisDvsEvent.x + "/" + davisDvsEvent.y + "/" + davisDvsEvent.i);
      System.out.println("Current timestamp: " + davisDvsEvent.time);
      System.out.println("*********************************************************************");
    }
  }

  // return number of active/hidden blobs
  private int getNumberOfBlobs(boolean whichLayer) {
    int quantity = 0;
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getLayerID() == whichLayer) {
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
}
