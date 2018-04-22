// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.util.List;

import ch.ethz.idsc.demo.mg.TrackedBlobIO;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// this class provides a evaluation of the tracking algorithm performance. The ground truth is loaded and compared with
// the position of the tracked features as provided by the tracking algorithm.
public class TrackingEvaluator {
  List<List<TrackedBlob>> labeledFeatures; // contains the ground truth
  int[] timeStamps; // contains timestamps for which ground truth is available
  DavisBlobTracker track;
  int currentLabelInstant = 0;
  int numberOfLabelInstants = 0;

  TrackingEvaluator(File pathToFile, DavisBlobTracker track) {
    labeledFeatures = TrackedBlobIO.loadFeatures(pathToFile);
    numberOfLabelInstants = labeledFeatures.size();
    System.out.println(numberOfLabelInstants);
    this.track = track;
    timeStamps = new int[numberOfLabelInstants];
    for (int i = 0; i < numberOfLabelInstants; i++) {
      timeStamps[i] = labeledFeatures.get(i).get(0).getTimeStamp();
    }
  }

  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    if (davisDvsEvent.time == timeStamps[currentLabelInstant]) {
      System.out.println("Performance evaluation instant happening now!");
      evaluatePerformance();
      if (currentLabelInstant < numberOfLabelInstants - 1) {
        currentLabelInstant++;
      }
    }
  }

  // compare estimated with ground truth features and calculate performance metric
  private void evaluatePerformance() {
    List<TrackedBlob> estimatedFeatures = track.getBlobList(1);
    List<TrackedBlob> groundTruthFeatures = labeledFeatures.get(currentLabelInstant);
  }
}
