// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.PrimitivesIO;

/** sets up the SLAM algorithm to process an offline log file */
class SlamSetup {
  private final SlamConfig slamConfig;
  private final String logFileName;
  private final File logFile;
  private final Long logFileDuration;
  private final boolean saveSlamMap;
  private final boolean localizationMode;

  SlamSetup(SlamConfig slamConfig) {
    this.slamConfig = slamConfig;
    logFileName = slamConfig.davisConfig.logFileName;
    logFile = slamConfig.davisConfig.getLogFile();
    logFileDuration = slamConfig.davisConfig.maxDuration.number().longValue() * 1000;
    saveSlamMap = slamConfig.saveSlamMap;
    localizationMode = slamConfig.localizationMode;
  }

  private void runAlgo() {
    try {
      OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap(slamConfig);
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlineSlamWrap);
      if (saveSlamMap && !localizationMode) {
        PrimitivesIO.saveToCSV( //
            SlamFileLocations.recordedMaps(logFileName), //
            offlineSlamWrap.getSlamProvider().getMap(0).getMapArray());
        System.out.println("Slam map successfully saved");
      }
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
