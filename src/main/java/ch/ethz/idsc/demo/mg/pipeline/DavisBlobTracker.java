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
  private static final int initVariance = 100;
  // algorithm parameters
  private final float aUp = 2f; // if activity is higher, blob is in active layer
  private final float aDown = 0.01f; // if activity is lower, blob gets deleted
  private final float scoreThreshold = 0.1f; // score threshold for active blobs
  private final float alphaOne = 0.1f; // for blob position update
  private final float alphaTwo = 0.01f; // for blob covariance update
  private final float tau = 50000; // [us] tunes activity update
  private final float alphaAttr = 0.01f; // hidden blobs attraction
  private final int dMax = 40; // [pixel] hidden blobs attraction
  // tracker operates on an ArrayList of tracked objects
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
    System.out.println(blobs.size()+" number of blobs at initialization");
  }

  public void receiveNewEvent(DavisDvsEvent davisDvsEvent) {
    // update matching blob with event data
    int activeBlob = calcScoreAndParams(davisDvsEvent);
    System.out.println(activeBlob+ " active blob number");
    System.out.println(blobs.size()+ " number of blobs");
    // update activity of blobs
    updateActivity(davisDvsEvent, activeBlob);
    // promote blobs and fill hidden layer up again
    promoteBlobs();
    // TODO for every pair of blobs correct with repulsion equation
    // blobs.updateRepulsionEquation(davisDvsEvent);
    // for all blobs of hidden layer correct with attraction equation
    hiddenBlobAttraction();
    // remove blobs that are not tracked anymore
    deleteBlobs();
    // TODO put processed tracked blobs in appropriate data structure and send it to
    // next module
    // update the timestamp. do it at the end so always last timestamp is stored.
    DavisSingleBlob.updateTimestamp(davisDvsEvent);
  }

  // some function to return the tracked blob position for further processing
  void returnBlobsLocations() {
    // bla bla
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
    System.out.println(highScore+" active blob highscore");
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
      System.out.println(hiddenHighScore+" hidden blob highscore");
      blobs.get(hiddenHighScoreBlob).updateBlobParameters(davisDvsEvent, alphaOne, alphaTwo);
      return hiddenHighScoreBlob;
    }
  }

  // update activity of blobs. blob that is associated with event is updated differently.
  private void updateActivity(DavisDvsEvent davisDvsEvent, int activeBlob) {
    blobs.get(activeBlob).updateBlobActivity(davisDvsEvent, tau, true);
    System.out.println(blobs.get(activeBlob).getActivity()+" activity of active Blob");
    for (int i = 0; i < blobs.size(); i++) {
      if (i != activeBlob) {
        blobs.get(i).updateBlobActivity(davisDvsEvent, tau, false);
      }
    }
  }

  // if blob is promoted, we add a new hidden blob at its initial position.
  private void promoteBlobs() {
    // store blob size since it will be modified inside for loop
    int blobSize = blobs.size();
    for (int i = 0; i < blobSize; i++) {
      if (blobs.get(i).blobPromotion(aUp)) {
        // put element at the end of the list and promote it to active layer
        blobs.add(blobs.get(i));
        System.out.println(blobs.size()+" updated blob size");
        blobs.get(blobs.size()-1).setLayerID(true); 
        // replace the promoted blob with a new initial blob
        float[] oldInitPos = blobs.get(i).getInitPos();
        DavisSingleBlob newInitBlob = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
        blobs.set(i, newInitBlob);
      }
    }
  }

  private void hiddenBlobAttraction() {
    for (int i = 0; i < blobs.size(); i++) {
      if (!blobs.get(i).getLayerID()) {
        blobs.get(i).updateAttractionEquation(alphaAttr, dMax);
      }
    }
  }

  // delete all blobs with low activity or out of bounds
  // TODO: maybe put blob at initial position again?
  private void deleteBlobs() {
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getActivity() < aDown || blobs.get(i).isOutOfBounds()) {
        //if not enough blobs, just put it at initial position
        if(blobs.size() < 24) {
          float[] oldInitPos = blobs.get(i).getInitPos();
          DavisSingleBlob newInitBlob = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
          blobs.set(i, newInitBlob);
          System.out.println( "blob set to initial position");
        }
        else {          
          blobs.remove(i);
          System.out.println("blob deleted");
        }
      }
    }
  }
}
