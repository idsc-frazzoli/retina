// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.demo.mg.eval.TrackingEvaluator;

/** pipeline setup for single/multirun */
public class PipelineSetup {
  private PipelineConfig pipelineConfig;
  private TrackingEvaluator evaluator;
  private int iterationLength;

  PipelineSetup(PipelineConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
    iterationLength = pipelineConfig.iterationLength.number().intValue();
  }

  private void iterate() {
    // no visualization for multirun
    for (int i = 0; i < iterationLength; i++) {
      System.out.println("****new Iteration ****");
      // modify CSV file name where estimated features are saved for each iteration
      // for evaluation, use TrackingEvaluator as standalone application
      pipelineConfig.estimatedLabelFileName = pipelineConfig.logFileName.toString() + "_run_" + i;
      runPipeline();
    }
  }

  private void runPipeline() {
    // get logFile
    File logFile = pipelineConfig.getLogFile();
    try {
      System.out.println("****Begin of pipeline run****");
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
