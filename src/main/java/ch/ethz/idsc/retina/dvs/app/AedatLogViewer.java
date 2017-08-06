// code by jph
package ch.ethz.idsc.retina.dvs.app;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.dev.davis._240c.EventRealtimeSleeper;

public enum AedatLogViewer {
  ;
  // TODO arguments not final ...
  public static void of(DavisEventProvider davisEventProvider, DavisDecoder davisDecoder, DavisDevice davisDevice, double speed) throws IOException {
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addListener(davisEventStatistics);
    DefaultDavisDisplay davisImageDisplay = new DefaultDavisDisplay();
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    davisImageProvider.addListener(davisImageDisplay.apsRenderer);
    davisDecoder.addListener(davisImageProvider);
    // ---
    AccumulateDvsImage accumulateDvsImage = new AccumulateDvsImage(davisDevice, 50000);
    davisDecoder.addListener(accumulateDvsImage);
    accumulateDvsImage.addListener(davisImageDisplay.dvsRenderer);
    // ---
    davisDecoder.addListener(new EventRealtimeSleeper(speed));
    // ---
    davisEventProvider.start();
    davisEventProvider.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
