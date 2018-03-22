//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** This class implements an algorithm for Gaussian blob tracking as
 * described in the paper "asynchronous event-based multikernel
 * algorithm for high-speed visual features tracking. */
public class DavisBlobTracker {
  private static final int WIDTH = 240; //maybe import those values from other file?
  private static final int HEIGHT = 180;
  private static final int initNumberOfBlobs = 24;
  private static final int numberRows = 6; // on how many rows are the blobs initially distributed
  private static final int initVariance = 900;

  // first, all the algorithm parameters.
  private final float aUp = 0.15f;; // if activity is higher, blob is in active layer
  private final float aDown = 0.1f; // if activity is lower, blob gets deleted
  private final double scoreThreshold = 0.1; // score threshold below which event is ignored
  private final float alphaOne = 0.1f; // update parameter for blob position
  private final float alphaTwo = 0.01f; // update parameter for blob covariance
  private final int tau = 5000; // [us] parameter tunes activity update
  private final float alphaAttr = 0.01f; // attraction of hidden blobs
  private final int dMax = 40; // [pixel] parameter for attraction equation of hidden blobs
  // further fields
  private int quantity; // number of blobs that is tracked.
  List<DavisSingleBlob> blobs;

  // initialize the tracker with all blobs uniformly distributed on FoV.
  DavisBlobTracker() {
    quantity = initNumberOfBlobs;
    blobs = new ArrayList<DavisSingleBlob>();
    int rowSpacing = WIDTH/(numberRows+1);
    int columnSpacing = HEIGHT/(initNumberOfBlobs/numberRows + 1);
    for (int i = 0; i < quantity; i++) {
      int column = (i % numberRows) + 1; // vary the 
      int row = i / numberRows + 1; // use integer division
      blobs[i] = new DavisSingleBlob(column * columnSpacing, row * rowSpacing, initVariance);
    }
  }

  public void receiveNewEvent(DavisDvsEvent davisDvsEvent) {
    // update matching blob with event data
    updateParamsAndActivity(davisDvsEvent);
    // decide if blob is in active or hidden layer
    promoteBlobs();
    // for every pair of blobs correct with repulsion equation
    blobs[0].updateRepulsionEquation(davisDvsEvent);
    // for all blobs of hidden layer correct with attraction equation
    hiddenBlobAttraction();
    // remove blobs that are not tracked anymore
    deleteBlobs();
    // update the timestamp. do it at the end so always last timestamp is stored.
    DavisSingleBlob.updateTimestamp(davisDvsEvent);
  }

  // some function to return the tracked blob position for further processing
  void returnBlobsLocations() {
    // bla bla
  }

  private void updateParamsAndActivity(DavisDvsEvent davisDvsEvent) {
    double highScore = 0;
    int highScoringBlob = 0;
    // calculate score
    for (int i = 0; i < quantity; i++) {
      float individualScore = blobs[i].calculateBlobScore(davisDvsEvent);
      // store highest score and which blob it belongs to
      if (individualScore > highScore) {
        highScore = individualScore;
        highScoringBlob = i;
      }
    }
    // for blob with best score, update parameters and activity
    
    if (highScore > scoreThreshold) {
      blobs[highScoringBlob].updateBlobParameters(davisDvsEvent, alphaOne, alphaTwo);
      blobs[highScoringBlob].updateBlobActivity(davisDvsEvent, tau, true);
    }
    // update activity for all remaining blobs in active layer
    // TODO why only blobs in active layer? Would make more sense to update for all blobs.
    for (int i = 0; i < quantity; i++) {
      if (blobs[i].getLayerID() == 0 && i != highScoringBlob) {
        blobs[i].updateBlobActivity(davisDvsEvent, tau, false);
      }
    }
  }

  private void promoteBlobs() {
    for (int i = 0; i < quantity; i++) {
      if (blobs[i].blobPromotion(aUp)) {
        // save initial position and create a new seed blob
        float[] oldInitPos = blobs[i].getInitPos();
        //the blob index is now the highest one
        blobs
        
        blobs[i] = new DavisSingleBlob(oldInitPos[0], oldInitPos[1], initVariance);
        
      }
    }
  }

  private void hiddenBlobAttraction() {
    for (int i = 0; i < quantity; i++) {
      if (blobs[i].getLayerID() == 1) {
        blobs[i].updateAttractionEquation(alphaAttr, dMax);
      }
    }
  }

  // delete all blobs with low activity or out of bounds
  private void deleteBlobs() {
    boolean[] deleting = new boolean[quantity];
    for (int i = 0; i < quantity; i++) {
      if (blobs[i].getActivity() < aDown || blobs[i].isOutOfBounds()) {
        deleting[i] = true;
      }
    }
    // TODO delete here all trackedBlobs where i=true
  }
}
