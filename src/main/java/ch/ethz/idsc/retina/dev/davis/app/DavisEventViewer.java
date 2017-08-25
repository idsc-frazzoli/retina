// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.dev.davis._240c.DavisRealtimeSleeper;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameCollector;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public enum DavisEventViewer {
  ;
  // TODO arguments not final ...
  // TODO code somewhat redundant to DavisDatagramClientDemo
  public static void of(StartAndStoppable davisEventProvider, DavisDecoder davisDecoder, DavisDevice davisDevice, double speed) throws IOException {
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addDvsListener(davisEventStatistics);
    davisDecoder.addSigListener(davisEventStatistics);
    davisDecoder.addImuListener(davisEventStatistics);
    DavisViewerFrame davisImageDisplay = new DavisViewerFrame(Davis240c.INSTANCE); // TODO
    davisImageDisplay.setStatistics(davisEventStatistics);
    // handle dvs
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(davisDevice, 50000);
    davisDecoder.addDvsListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisImageDisplay);
    // handle aps
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    davisImageProvider.addListener(davisImageDisplay);
    davisImageProvider.addListener(new DavisApsStatusWarning());
    davisDecoder.addSigListener(davisImageProvider);
    // handle imu
    DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
    davisImuFrameCollector.addListener(davisImageDisplay);
    davisDecoder.addImuListener(davisImuFrameCollector);
    // ---
    if (0 < speed)
      davisDecoder.addImuListener(new DavisRealtimeSleeper(speed));
    // ---
    davisEventProvider.start();
    davisEventProvider.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
