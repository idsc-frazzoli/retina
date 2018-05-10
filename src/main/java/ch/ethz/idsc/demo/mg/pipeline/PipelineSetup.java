// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.tensor.RealScalar;

/** pipeline setup for single/multirun */
public class PipelineSetup {
  private PipelineConfig pipelineConfig;
  private InputSubModule inputSubModule;
  private boolean multiRunPipeline;

  PipelineSetup(PipelineConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
  }

  private void iterate() {
    // no visualization for multirun
    pipelineConfig.visualizePipeline = RealScalar.of(0);
    for (int i = 0; i < 5; i++) {
      System.out.println("*************new Iteration **************");
      pipelineConfig.aUp = RealScalar.of(0.1 + i * 0.03);
      runPipeline();
      // somehow collect results here
      inputSubModule.collectResults();
    }
  }

  private void runPipeline() {
    // get logFile
    File logFile = pipelineConfig.getLogFile();
    try {
      System.out.println("****Begin of pipeline run****");
      // initialize inputSubModule with current config and run logplayer
      inputSubModule = new InputSubModule(pipelineConfig);
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
    pipelineSetup.multiRunPipeline = false;
    if (pipelineSetup.multiRunPipeline) {
      pipelineSetup.iterate();
    } else {
      pipelineSetup.runPipeline();
    }
  }
}
