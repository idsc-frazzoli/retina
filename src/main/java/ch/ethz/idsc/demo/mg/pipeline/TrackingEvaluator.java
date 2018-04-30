// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.gui.HandLabeler;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

// this class provides a evaluation of the tracking algorithm performance. The ground truth is loaded from a hand labeled .csv file
// and then compared with the tracking algorithm during runtime.
public class TrackingEvaluator {
  List<List<ImageBlob>> labeledFeatures; // contains handlabeled features
  int[] timeStamps; // timestamps for which handlabeld features are available
  int currentLabelInstant = 0;
  int numberOfLabelInstants = 0;
  int distanceForAgreement = 20; // [pixel]

  // load the labeled data
  TrackingEvaluator(File file) {
    setTimestampsFromCSV(file);
    numberOfLabelInstants = timeStamps.length;
    labeledFeatures = HandLabeler.loadFromCSV(file, timeStamps);
  }

  public boolean isGroundTruthAvailable(DavisDvsEvent davisDvsEvent) {
    if (currentLabelInstant <= numberOfLabelInstants - 1) {
      if (davisDvsEvent.time == timeStamps[currentLabelInstant]) {
        return true;
      }
    }
    return false;
  }

  // compare estimated with ground truth features and calculate performance metric
  public void evaluatePerformance(List<ImageBlob> estimatedFeatures) {
    System.out.println("Performance evaluation instant happening now!");
    List<ImageBlob> groundTruthFeatures = labeledFeatures.get(currentLabelInstant);
    // compare the two lists somehow
    //..
    // increase count
    currentLabelInstant++;
  }

  // extract timeStamps from .csv file
  private void setTimestampsFromCSV(File file) {
    // use list because length is unknown
    List<Integer> timestampList = new ArrayList<>();
    try {
      Tensor inputTensor = Import.of(file);
      // initialize extractedTimestamps
      timestampList.add(inputTensor.get(0).Get(0).number().intValue());
      for (int i = 1; i < inputTensor.length(); i++) {
        if (inputTensor.get(i).Get(0).number().intValue() != timestampList.get(timestampList.size() - 1)) {
          timestampList.add(inputTensor.get(i).Get(0).number().intValue());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // convert list to array
    int[] timeStamps = new int[timestampList.size()];
    for (int i = 0; i < timestampList.size(); i++) {
      timeStamps[i] = timestampList.get(i);
    }
    this.timeStamps = timeStamps;
  }
}
