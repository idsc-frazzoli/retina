// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.blobtrack.algo.ImageBlobSelector;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** this class saves the estimatedFeatures at timestamps when hand-labeled ground truth is available and saves
 * finally to a CSV file for further analysis with TrackingEvaluator */
// TODO MG if no features are hand-labeled at a certain instant, we do not collect the estimatedFeatures at that instant.
public class TrackingCollector implements DavisDvsListener {
  private final ImageBlobSelector imageBlobSelector;
  private final String imagePrefix;
  private final String estimatedLabelFileName;
  private final File estimatedLabelFile;
  private final List<List<ImageBlob>> estimatedFeatures;
  private final int numberOfLabelInstants;
  private final int[] timeStamps; // timestamps for which hand-labeled features are available
  private int currentLabelInstant = 0;

  public TrackingCollector(ImageBlobSelector imageBlobSelector, BlobTrackConfig pipelineConfig) {
    this.imageBlobSelector = imageBlobSelector;
    imagePrefix = pipelineConfig.davisConfig.logFilename();
    numberOfLabelInstants = MgEvaluationFolders.HANDLABEL.subfolder(imagePrefix).list().length;
    timeStamps = EvalUtil.getTimestampsFromImages(numberOfLabelInstants, imagePrefix);
    estimatedLabelFileName = pipelineConfig.estimatedLabelFileName.toString();
    estimatedLabelFile = EvaluationFileLocations.ESTIMATED_CSV.subfolder(estimatedLabelFileName);
    // set up empty list of estimated features
    estimatedFeatures = new ArrayList<>();
    for (int i = 0; i < timeStamps.length; ++i)
      estimatedFeatures.add(new ArrayList<>());
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (isGroundTruthAvailable(davisDvsEvent))
      setEstimatedFeatures(imageBlobSelector.getSelectedBlobs());
  }

  private boolean isGroundTruthAvailable(DavisDvsEvent davisDvsEvent) {
    return currentLabelInstant <= numberOfLabelInstants - 1 //
        && davisDvsEvent.time == timeStamps[currentLabelInstant];
  }

  public void setEstimatedFeatures(List<ImageBlob> estimatedFeaturesInstant) {
    System.out.println("Estimated features are collected. Instant nr " + (currentLabelInstant + 1));
    estimatedFeatures.set(currentLabelInstant, estimatedFeaturesInstant);
    // counter
    ++currentLabelInstant;
    if (currentLabelInstant == numberOfLabelInstants) {
      EvalUtil.saveToCSV(estimatedLabelFile, estimatedFeatures, timeStamps);
      System.out.println("Estimated labels saved to " + estimatedLabelFileName);
    }
  }
}
