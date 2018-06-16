// code by jph
package ch.ethz.idsc.demo.jph;

import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.api.LogFile;
import ch.ethz.idsc.gokart.offline.tab.SteerRelRckWatchdog;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;

/** starting on 20180607 the steering system exhibited behavior out of the nominal range */
/* package */ enum SteerRelRckCheck {
  ;
  public static void main(String[] args) {
    // LogFile logFile = GokartLogFile._20180614T142228_6a2f62c6;
    for (LogFile logFile : DatahakiLogFileLocator.all())
      try {
        System.out.println(logFile.getTitle());
        SteerRelRckWatchdog steerRelRckWatchdog = new SteerRelRckWatchdog();
        OfflineLogPlayer.process(DatahakiLogFileLocator.file(logFile), steerRelRckWatchdog);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
