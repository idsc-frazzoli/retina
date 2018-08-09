// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;

/** pipeline setup for single/multirun of logfiles
 * SLAM algorithm is also set up here */
/* package */ class PipelineSetup {
  private final PipelineConfig pipelineConfig;
  private final int iterationLength;

  PipelineSetup(PipelineConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
    iterationLength = pipelineConfig.iterationLength.number().intValue();
  }

  private void iterate() {
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("******** Iteration nr " + (i + 1));
      double aUp = 0.08 + i * 0.01;
      String newEstimatedLabelFileName = pipelineConfig.davisConfig.logFileName.toString() + "_aUp_" + aUp;
      pipelineConfig.aUp = RealScalar.of(aUp);
      pipelineConfig.estimatedLabelFileName = newEstimatedLabelFileName;
      runPipeline();
    }
  }

  private void runPipeline() {
    File logFile = pipelineConfig.davisConfig.getLogFile();
    long logFileDuration = Magnitude.MICRO_SECOND.apply(pipelineConfig.davisConfig.maxDuration).number().longValue();
    try {
      // initialize offlinePipelineWrap with current pipelineConfig
      OfflinePipelineWrap offlinePipelineWrap = new OfflinePipelineWrap(pipelineConfig);
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlinePipelineWrap);
      // show summary
      offlinePipelineWrap.summarizeLog();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    // initialize pipelineConfig -- could also load existing pipelineConfig
    PipelineConfig pipelineConfig = new PipelineConfig();
    // pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
    PipelineSetup pipelineSetup = new PipelineSetup(pipelineConfig);
    // multirun for tracking evaluation
    if (pipelineConfig.collectEstimatedFeatures) {
      pipelineSetup.iterate();
    } else {
      pipelineSetup.runPipeline();
    }
  }
}
