// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.io.IOException;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis.DavisEventProvider;
import ch.ethz.idsc.retina.davis._240c.ApsStatusWarning;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.davis._240c.EventRealtimeSleeper;

public enum DavisEventViewer {
  ;
  // TODO arguments not final ...
  public static void of(DavisEventProvider davisEventProvider, DavisDecoder davisDecoder, DavisDevice davisDevice, double speed) throws IOException {
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addListener(davisEventStatistics);
    DavisDefaultDisplay davisImageDisplay = new DavisDefaultDisplay();
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    davisImageProvider.addListener(davisImageDisplay.apsRenderer);
    davisImageProvider.addListener(new ApsStatusWarning());
    davisDecoder.addListener(davisImageProvider);
    // ---
    AccumulateDvsImage accumulateDvsImage = new AccumulateDvsImage(davisDevice, 50000);
    davisDecoder.addListener(accumulateDvsImage);
    accumulateDvsImage.addListener(davisImageDisplay.dvsRenderer);
    // ---
    if (0 < speed)
      davisDecoder.addListener(new EventRealtimeSleeper(speed));
    // ---
    davisEventProvider.start();
    davisEventProvider.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
