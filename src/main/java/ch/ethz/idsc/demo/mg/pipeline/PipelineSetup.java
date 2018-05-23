// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.tensor.RealScalar;

/** pipeline setup for single/multirun */
public class PipelineSetup {
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
      // initialize inputSubModule with current config and run logplayer
      InputSubModule inputSubModule = new InputSubModule(pipelineConfig);
      BoundedOfflineLogPlayer.process(logFile, //
          pipelineConfig.maxDuration.number().longValue() * 1000, inputSubModule);
      // show summary
      inputSubModule.summarizeLog();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    // initialize config -- could also load existing config
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
