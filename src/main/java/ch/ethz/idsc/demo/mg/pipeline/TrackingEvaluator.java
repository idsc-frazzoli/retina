// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.HandLabelFileLocations;
import ch.ethz.idsc.demo.mg.gui.HandLabeler;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

// this class provides a evaluation of the tracking algorithm performance. The ground truth is loaded from a hand labeled .CSV file
// and then compared with the tracking algorithm during runtime.
public class TrackingEvaluator {
  private String handLabelFileName;
  private File handLabelFile;
  private List<List<ImageBlob>> labeledFeatures; // contains handlabeled features
  private int[] timeStamps; // timestamps for which handlabeld features are available
  private int currentLabelInstant = 0;
  private int numberOfLabelInstants = 0;
  private int distanceForAgreement = 20; // [pixel]
  // visualization
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);

  // load the labeled data
  TrackingEvaluator(PipelineConfig pipelineConfig) {
    handLabelFileName = pipelineConfig.handLabelFileName.toString();
    handLabelFile = HandLabelFileLocations.labels(handLabelFileName);
    setTimestampsFromCSV(handLabelFile);
    numberOfLabelInstants = timeStamps.length;
    labeledFeatures = HandLabeler.loadFromCSV(handLabelFile, timeStamps);
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
  // first version: we only care about feature position
  public void evaluatePerformance(List<ImageBlob> estimatedFeatures) {
    System.out.println("Performance evaluation instant happening now!");
    List<ImageBlob> groundTruthFeatures = labeledFeatures.get(currentLabelInstant);
    // array to save distance and feature number of closest estimate
    float[][] distToClosestEstimate = new float[groundTruthFeatures.size()][2];
    // TODO initialize to large distance and feature number that is impossible
    // ..
    // iterate through all combinations
    for (int i = 0; i < groundTruthFeatures.size(); i++) {
      for (int j = 0; j < estimatedFeatures.size(); j++) {
        float currentDist = computeDistance(groundTruthFeatures.get(i), estimatedFeatures.get(j));
        if (currentDist < distToClosestEstimate[i][0]) {
          distToClosestEstimate[i][0] = currentDist;
          distToClosestEstimate[i][1] = j;
        }
      }
    }
    // setup for logic:
    // if distToClosestEstimate[i][0] is smaller than threshold, we have a Tp
    // if distToClosestEstimate[i][0] is larger than threshold, we have a Fn
    // all estimatedFeatures that are not assigned true positive are therefore Fp
    
    // precision: Tp/(Tp+Fp)
    // recall: Tp/(Tp+Fn)
    
    // counter
    currentLabelInstant++;
  }
  
  // TODO we need some nice visualization for the evaluation moments: labeled features overlaid on labeledEventframe with the estimated 
  // features in another color also overlaid
  private void performanceVisualization() {
    // load matching accumulatedEventFrame (very similar as in HandLabeler)
    // draw groundtruthFeatures (like in Handlabeler)
    // overlay estimatedFeatures
    // save frames
  }

  // extract timeStamps from .CSV file
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

  private static float computeDistance(ImageBlob firstBlob, ImageBlob secondBlob) {
    float distance = 0;
    return distance; // TODO implementation incomplete
  }
}
