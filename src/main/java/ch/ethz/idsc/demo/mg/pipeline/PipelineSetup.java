// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.tensor.RealScalar;

/** pipeline setup for single/multirun of logfiles */
/* package */ class PipelineSetup {
  private PipelineConfig pipelineConfig;
  private final int iterationLength;

  PipelineSetup(PipelineConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
    iterationLength = pipelineConfig.iterationLength.number().intValue();
  }

  private void iterate() {
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("******** Iteration nr " + (i + 1));
      int newTau = 1000 + i * 1000;
      String newEstimatedLabelFileName = pipelineConfig.logFileName.toString() + "_tau_" + newTau;
      pipelineConfig.tau = RealScalar.of(newTau);
      pipelineConfig.estimatedLabelFileName = newEstimatedLabelFileName;
      runPipeline();
    }
  }

  private void runPipeline() {
    File logFile = pipelineConfig.getLogFile();
    try {
      // initialize offlinePipelineWrap with current pipelineConfig
      OfflinePipelineWrap offlinePipelineWrap = new OfflinePipelineWrap(pipelineConfig);
      BoundedOfflineLogPlayer.process(logFile, //
          pipelineConfig.maxDuration.number().longValue() * 1000, offlinePipelineWrap);
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
