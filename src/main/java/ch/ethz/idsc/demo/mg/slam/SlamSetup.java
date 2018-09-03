// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;

import ch.ethz.idsc.demo.BoundedOfflineLogPlayer;
import ch.ethz.idsc.retina.util.io.PrimitivesIO;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** sets up the SLAM algorithm for offline processing of a log file */
/* package */ class SlamSetup {
  private final SlamConfig slamConfig;
  private final String logFilename;
  private final File logFile;
  private final boolean saveSlamMap;
  private final long logFileDuration;

  SlamSetup(SlamConfig slamConfig) {
    this.slamConfig = slamConfig;
    logFilename = slamConfig.davisConfig.logFilename();
    logFile = slamConfig.davisConfig.getLogFile();
    logFileDuration = Magnitude.MICRO_SECOND.toLong(slamConfig.davisConfig.logFileDuration);
    saveSlamMap = slamConfig.saveSlamMap;
  }

  private void runAlgo() {
    OfflineSlamWrap offlineSlamWrap = new OfflineSlamWrap(slamConfig);
    try {
      BoundedOfflineLogPlayer.process(logFile, logFileDuration, offlineSlamWrap);
      if (saveSlamMap)
        saveOccurrenceMap(offlineSlamWrap);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void saveOccurrenceMap(OfflineSlamWrap offlineSlamWrap) {
    PrimitivesIO.saveToCSV( //
        SlamFileLocations.recordedMaps(logFilename), //
        offlineSlamWrap.getSlamContainer().getOccurrenceMap().getMapArray());
    System.out.println("Slam map successfully saved");
  }

  public static void main(String[] args) {
    SlamConfig slamConfig = new SlamConfig();
    SlamSetup slamSetup = new SlamSetup(slamConfig);
    slamSetup.runAlgo();
  }
}
