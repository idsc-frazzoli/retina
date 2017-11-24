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
  // TODO code somewhat redundant to DavisDatagramClientDemo
  public static void of(StartAndStoppable davisEventProvider, DavisDecoder davisDecoder, DavisDevice davisDevice, double speed) throws IOException {
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addDvsListener(davisEventStatistics);
    davisDecoder.addSigListener(davisEventStatistics);
    davisDecoder.addImuListener(davisEventStatistics);
    DavisViewerFrame davisViewerFrame = new DavisViewerFrame(Davis240c.INSTANCE);
    davisViewerFrame.setStatistics(davisEventStatistics);
    // handle dvs
    AccumulatedEventsGrayImage accumulatedEventsImage = new AccumulatedEventsGrayImage(davisDevice, 50000);
    davisDecoder.addDvsListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisViewerFrame.davisViewerComponent.dvsImageListener);
    // handle aps
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    davisImageProvider.addListener(davisViewerFrame.davisViewerComponent.sigListener);
    davisImageProvider.addListener(new DavisApsStatusWarning());
    davisDecoder.addSigListener(davisImageProvider);
    // handle imu
    DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
    davisImuFrameCollector.addListener(davisViewerFrame.davisViewerComponent);
    davisDecoder.addImuListener(davisImuFrameCollector);
    // ---
    if (0 < speed)
      davisDecoder.addImuListener(new DavisRealtimeSleeper(speed));
    // ---
    davisEventProvider.start();
    davisEventProvider.stop();
    davisEventStatistics.print();
    // davisViewerFrame.close();
  }
}
