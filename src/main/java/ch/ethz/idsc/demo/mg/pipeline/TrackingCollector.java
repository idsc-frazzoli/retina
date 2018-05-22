// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.eval.EvaluationFileLocations;
import ch.ethz.idsc.demo.mg.util.CSVUtil;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// this class saves the estimatedFeatures at timestamps when handlabeled ground truth is available and saves
// finally to a CSV file for further analysis with TrackingEvaluator
public class TrackingCollector {
  private final File handLabelFile;
  private final File estimatedLabelFile;
  private final List<List<ImageBlob>> estimatedFeatures;
  private final int numberOfLabelInstants;
  private int[] timeStamps; // timestamps for which handlabeld features are available
  private int currentLabelInstant = 0;

  TrackingCollector(PipelineConfig pipelineConfig) {
    handLabelFile = EvaluationFileLocations.handlabels(pipelineConfig.handLabelFileName.toString());
    estimatedLabelFile = EvaluationFileLocations.estimatedlabels(pipelineConfig.estimatedLabelFileName.toString());
    timeStamps = CSVUtil.getTimestampsFromCSV(handLabelFile);
    numberOfLabelInstants = timeStamps.length;
    // set up empty list of estimated features
    estimatedFeatures = new ArrayList<>(numberOfLabelInstants);
    for (int i = 0; i < timeStamps.length; i++) {
      List<ImageBlob> emptyList = new ArrayList<>();
      estimatedFeatures.add(emptyList);
    }
  }

  public boolean isGroundTruthAvailable(DavisDvsEvent davisDvsEvent) {
    if (currentLabelInstant <= numberOfLabelInstants - 1) {
      if (davisDvsEvent.time == timeStamps[currentLabelInstant]) {
        return true;
      }
    }
    return false;
  }

  public void setEstimatedFeatures(List<ImageBlob> estimatedFeaturesInstant) {
    System.out.println("Estimated features are collected. Instant nr "+currentLabelInstant+1);
    estimatedFeatures.set(currentLabelInstant, estimatedFeaturesInstant);
    // counter
    currentLabelInstant++;
    if (currentLabelInstant == numberOfLabelInstants) {
      CSVUtil.saveToCSV(estimatedLabelFile, estimatedFeatures, timeStamps);
    }
  }
}
