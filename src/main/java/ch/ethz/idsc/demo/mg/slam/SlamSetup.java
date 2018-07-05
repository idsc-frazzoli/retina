// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;

public class SlamSetup {
  private final SlamConfig slamConfig;

  SlamSetup(SlamConfig slamConfig) {
    this.slamConfig =slamConfig;
  }

  private void runAlgo() {
    File logFile = slamConfig.davisConfig.getLogFile();
    Long logFileDuration = slamConfig.davisConfig.maxDuration.number().longValue() * 1000;
    try {
      OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap(slamConfig);
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlineSlamWrap);
      if (slamConfig.saveSlamMap && !slamConfig.localizationMode)
        offlineSlamWrap.saveRecordedMap();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    SlamConfig slamConfig = new SlamConfig();
    SlamSetup slamSetup = new SlamSetup(slamConfig);
    slamSetup.runAlgo();
  }
}
