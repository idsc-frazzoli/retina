// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.io.IOException;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.davis._240c.DavisRealtimeSleeper;
import ch.ethz.idsc.retina.davis.imu.DavisImuFrameCollector;

public enum DavisEventViewer {
  ;
  // TODO arguments not final ...
  // TODO code somewhat redundant to DavisDatagramClientDemo
  public static void of(StartAndStoppable davisEventProvider, DavisDecoder davisDecoder, DavisDevice davisDevice, double speed) throws IOException {
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addListener(davisEventStatistics);
    DavisDefaultDisplay davisImageDisplay = new DavisDefaultDisplay();
    davisImageDisplay.setStatistics(davisEventStatistics);
    // handle dvs
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(davisDevice, 50000);
    davisDecoder.addListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisImageDisplay);
    // handle aps
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    davisImageProvider.addListener(davisImageDisplay);
    davisImageProvider.addListener(new DavisApsStatusWarning());
    davisDecoder.addListener(davisImageProvider);
    // handle imu
    DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
    davisImuFrameCollector.addListener(davisImageDisplay);
    davisDecoder.addListener(davisImuFrameCollector);
    // ---
    if (0 < speed)
      davisDecoder.addListener(new DavisRealtimeSleeper(speed));
    // ---
    davisEventProvider.start();
    davisEventProvider.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
