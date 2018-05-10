// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

// class handles interface with TrackingIterator and also used as standalone version to run pipeline
public class PipelineSetup {
  // ..
  private static void runPipeline(String pathToFile, PipelineConfig pipelineConfig) {
    File logFile = new File(pathToFile);
    InputSubModule inputSubModule = new InputSubModule(pipelineConfig);
    try {
      OfflineLogPlayer.process(logFile, inputSubModule);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // for standalone running of pipeline
  public static void main(String[] args) throws IOException {
    String pathToFile = LogFileLocations.DUBI10d;
    // could also load pipelineConfig from somewhere
    // TensorProperties.manifest(UserHome.file("config2.properties"), test);
    PipelineConfig pipelineConfig = new PipelineConfig();
    runPipeline(pathToFile, pipelineConfig);
  }
}
