// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.app.blob.BlobTrackConfig;
import ch.ethz.idsc.tensor.RealScalar;

/** sets up the blob tracking algorithm for offline processing of a log file */
/* package */ class BlobTrackSetup {
  private final BlobTrackConfig blobTrackConfig;
  private final String logFilename;
  private final File logFile;
  private final int iterationLength;

  BlobTrackSetup(BlobTrackConfig blobTrackConfig) {
    this.blobTrackConfig = blobTrackConfig;
    logFilename = blobTrackConfig.davisConfig.logFilename();
    logFile = blobTrackConfig.davisConfig.getLogFile();
    iterationLength = blobTrackConfig.iterationLength.number().intValue();
  }

  private void iterate() {
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("******** Iteration nr " + (i + 1));
      double aUp = 0.08 + i * 0.01;
      String newEstimatedLabelFileName = logFilename + "_aUp_" + aUp;
      blobTrackConfig.aUp = RealScalar.of(aUp);
      blobTrackConfig.estimatedLabelFileName = newEstimatedLabelFileName;
      runAlgo();
    }
  }

  private void runAlgo() {
    try {
      OfflineBlobTrackWrap offlinePipelineWrap = new OfflineBlobTrackWrap(blobTrackConfig);
      OfflineLogPlayer.process(logFile, offlinePipelineWrap);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    BlobTrackConfig blobTrackConfig = new BlobTrackConfig();
    BlobTrackSetup blobTrackSetup = new BlobTrackSetup(blobTrackConfig);
    if (blobTrackConfig.collectEstimatedFeatures)
      blobTrackSetup.iterate();
    else
      blobTrackSetup.runAlgo();
  }
}
