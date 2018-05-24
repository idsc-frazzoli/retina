// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.eval.EvaluationFileLocations;
import ch.ethz.idsc.demo.mg.util.CSVUtil;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// this class saves the estimatedFeatures at timestamps when hand-labeled ground truth is available and saves
// finally to a CSV file for further analysis with TrackingEvaluator
/* package */ class TrackingCollector {
  private final String estimatedLabelFileName;
  private final File handLabelFile;
  private final File estimatedLabelFile;
  private final List<List<ImageBlob>> estimatedFeatures;
  private final int numberOfLabelInstants;
  private int[] timeStamps; // timestamps for which hand-labeled features are available
  private int currentLabelInstant = 0;

  TrackingCollector(PipelineConfig pipelineConfig) {
    handLabelFile = EvaluationFileLocations.handlabels(pipelineConfig.handLabelFileName.toString());
    estimatedLabelFileName = pipelineConfig.estimatedLabelFileName.toString();
    estimatedLabelFile = EvaluationFileLocations.estimatedlabels(estimatedLabelFileName);
    timeStamps = CSVUtil.getTimestampsFromCSV(handLabelFile);
    numberOfLabelInstants = timeStamps.length;
    // set up empty list of estimated features
    estimatedFeatures = new ArrayList<>(numberOfLabelInstants);
    for (int i = 0; i < timeStamps.length; i++)
      estimatedFeatures.add(new ArrayList<>());
  }

  public boolean isGroundTruthAvailable(DavisDvsEvent davisDvsEvent) {
    return currentLabelInstant <= numberOfLabelInstants - 1 //
        && davisDvsEvent.time == timeStamps[currentLabelInstant];
  }

  public void setEstimatedFeatures(List<ImageBlob> estimatedFeaturesInstant) {
    System.out.println("Estimated features are collected. Instant nr " + (currentLabelInstant + 1));
    estimatedFeatures.set(currentLabelInstant, estimatedFeaturesInstant);
    // counter
    currentLabelInstant++;
    if (currentLabelInstant == numberOfLabelInstants) {
      CSVUtil.saveToCSV(estimatedLabelFile, estimatedFeatures, timeStamps);
      System.out.println("Estimated labels saved to " + estimatedLabelFileName);
    }
  }
}
