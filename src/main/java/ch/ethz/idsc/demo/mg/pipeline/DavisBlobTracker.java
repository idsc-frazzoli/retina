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
  private static final int initVariance = 900;
  // algorithm parameters
  private final float aUp = 2f; // if activity is higher, blob is in active layer
  private final float aDown = 0.01f; // if activity is lower, blob gets deleted
  private final float scoreThreshold = 0.1f; // score threshold for active blobs
  private final float alphaOne = 0.1f; // for blob position update
  private final float alphaTwo = 0.01f; // for blob covariance update
  private final float tau = 50000; // [us] tunes activity update
  private final float numberSigmas = 0.5f; // for out of bounds calculation
  private final float alphaAttr = 0.01f; // hidden blobs attraction
  private final float dAttr = 10; // [pixel] hidden blobs attraction
  private final float alphaRep = 0; // repulsion equation
  private final float dRep = 10; // repulstion equation
  // tracker operates on an ArrayList of DavisSingleBlobs
  List<DavisSingleBlob> blobs;

  // initialize the tracker with all blobs uniformly distributed
  DavisBlobTracker() {
    blobs = new ArrayList<DavisSingleBlob>();
    int rowSpacing = WIDTH / (numberRows + 1);
    int columnSpacing = HEIGHT / (initNumberOfBlobs / numberRows + 1);
    for (int i = 0; i < initNumberOfBlobs; i++) {
      int column = (i % numberRows) + 1; //
      int row = i / numberRows + 1; // use integer division
      DavisSingleBlob davisSingleBlob = new DavisSingleBlob(column * columnSpacing, row * rowSpacing, initVariance);
      blobs.add(davisSingleBlob);
    }
  }

  public void receiveNewEvent(DavisDvsEvent davisDvsEvent) {
    // update matching blob with event data
    int activeBlob = calcScoreAndParams(davisDvsEvent);
    // update activity of blobs
    boolean isPromoted = updateActivity(davisDvsEvent, activeBlob);
    // promote blob and put new hidden blob into hidden layer
    if (isPromoted) {
      promoteBlob(activeBlob);
    }
    // repulse blobs from each other
    calcRepulsion();
    // for all blobs of hidden layer use attraction equation
    hiddenBlobAttraction();
    // print some status news
    printStatusUpdate(activeBlob);
    // delete blobs if activity too low or out of bounds
    deleteBlobs();
    // TODO check if blobs are too small/ wrong shape and delete not fitting blobs
    // TODO put processed tracked blobs in appropriate data structure and send it to
    // next module
    // update the timestamp. do it at the end so always last timestamp is stored.
    DavisSingleBlob.updateTimestamp(davisDvsEvent);
  }

  private int calcScoreAndParams(DavisDvsEvent davisDvsEvent) {
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
      return highScoreBlob;
    }
    // if not, update best matching hidden blob
    else {
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
      return hiddenHighScoreBlob;
    }
  }

  // update activity of blobs. blob that is associated with event is updated differently.
  private boolean updateActivity(DavisDvsEvent davisDvsEvent, int activeBlob) {
    boolean isPromoted = blobs.get(activeBlob).updateBlobActivity(davisDvsEvent, tau, true, aUp);
    for (int i = 0; i < blobs.size(); i++) {
      if (i != activeBlob) {
        blobs.get(i).updateBlobActivity(davisDvsEvent, tau, false, aUp);
      }
    }
    return isPromoted;
  }

  // if blob is promoted, we add a new hidden blob at its initial position.
  private void promoteBlob(int activeBlob) {
    // put element at the end of the list and promote it to active layer
    blobs.add(blobs.get(activeBlob));
    blobs.get(blobs.size() - 1).setLayerID(true);
    // replace the promoted blob with a new initial blob
    float[] oldInitPos = blobs.get(activeBlob).getInitPos();
    DavisSingleBlob newInitBlob = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
    blobs.set(activeBlob, newInitBlob);
    // TODO blobs with activity between aUp and aDown are downgraded to hidden layer
  }


  // apply attraction equation
  private void hiddenBlobAttraction() {
    for (int i = 0; i < blobs.size(); i++) {
      if (!blobs.get(i).getLayerID()) {
        blobs.get(i).updateAttractionEquation(alphaAttr, dAttr);
      }
    }
  }

  // apply repulsion equation
  private void calcRepulsion() {
    // find each pair of two trackers
    for (int i = 0; i < (blobs.size() - 1); i++) {
      for (int j = i; j < blobs.size(); j++) {
        blobs.get(i).updateRepulsionEquation(alphaRep, dRep, blobs.get(j));
      }
    }
  }

  private void printStatusUpdate(int activeBlob) {
    // number and activities of active blobs
    System.out.println(blobs.size() + " blobs, with " + getActiveBlobs() + " being in active layer.");
    System.out.println(blobs.get(activeBlob).getActivity() + " activity of event-associated blob # " + activeBlob);

    // parameters of active blobs
  }

  // delete all blobs with low activity or out of bounds
  private void deleteBlobs() {
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getActivity() < aDown || blobs.get(i).isOutOfBounds(numberSigmas)) {
        // if not enough blobs, just put it at initial position
        if (blobs.size() <= initNumberOfBlobs) {
          float[] oldInitPos = blobs.get(i).getInitPos();
          DavisSingleBlob newInitBlob = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
          blobs.set(i, newInitBlob);
        } else {
          blobs.remove(i);
        }
      }
    }
  }

  // return number of active blobs
  private int getActiveBlobs() {
    int quantity = 0;
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getLayerID()) {
        quantity++;
      }
    }
    return quantity;
  }
}
