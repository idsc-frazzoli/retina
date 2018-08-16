// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import java.io.File;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.demo.mg.blobtrack.algo.BlobTrackConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** pipeline setup for single/multirun of log files */
/* package */ class BlobTrackSetup {
  private final BlobTrackConfig pipelineConfig;
  private final File logFile;
  private final Scalar logFileDuration;
  private final int iterationLength;

  BlobTrackSetup(BlobTrackConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
    logFile = pipelineConfig.davisConfig.getLogFile();
    logFileDuration = pipelineConfig.davisConfig.logFileDuration;
    iterationLength = pipelineConfig.iterationLength.number().intValue();
  }

  private void iterate() {
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("******** Iteration nr " + (i + 1));
      double aUp = 0.08 + i * 0.01;
      String newEstimatedLabelFileName = pipelineConfig.davisConfig.logFilename() + "_aUp_" + aUp;
      pipelineConfig.aUp = RealScalar.of(aUp);
      pipelineConfig.estimatedLabelFileName = newEstimatedLabelFileName;
      runPipeline();
    }
  }

  private void runPipeline() {
    try {
      OfflineBlobTrackWrap offlinePipelineWrap = new OfflineBlobTrackWrap(pipelineConfig);
      BoundedOfflineLogPlayer.process( //
          logFile, //
          Magnitude.MICRO_SECOND.toLong(logFileDuration), //
          offlinePipelineWrap);
      offlinePipelineWrap.summarizeLog();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    BlobTrackConfig pipelineConfig = new BlobTrackConfig();
    BlobTrackSetup pipelineSetup = new BlobTrackSetup(pipelineConfig);
    // multirun for tracking evaluation
    if (pipelineConfig.collectEstimatedFeatures) {
      pipelineSetup.iterate();
    } else {
      pipelineSetup.runPipeline();
    }
  }
}
