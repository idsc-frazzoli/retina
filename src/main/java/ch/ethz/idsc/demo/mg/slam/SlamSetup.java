// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;

public class SlamSetup {
  private final PipelineConfig pipelineConfig;

  SlamSetup(PipelineConfig pipelineConfig) {
    this.pipelineConfig = pipelineConfig;
  }

  private void runAlgo() {
    File logFile = pipelineConfig.getLogFile();
    Long logFileDuration = pipelineConfig.maxDuration.number().longValue() * 1000;
    try {
      OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap(pipelineConfig);
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlineSlamWrap);
      if (pipelineConfig.saveSlamMap && !pipelineConfig.localizationMode)
        offlineSlamWrap.saveRecordedMap();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    // initialize pipelineConfig -- could also load existing pipelineConfig
    PipelineConfig pipelineConfig = new PipelineConfig();
    // pipelineConfig = TensorProperties.retrieve(UserHome.file("config.properties"), new PipelineConfig());
    SlamSetup slamSetup = new SlamSetup(pipelineConfig);
    slamSetup.runAlgo();
  }
}
