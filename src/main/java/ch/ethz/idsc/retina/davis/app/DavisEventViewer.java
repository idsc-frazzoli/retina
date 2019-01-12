// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.davis._240c.DavisRealtimeSleeper;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameCollector;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public enum DavisEventViewer {
  ;
  // TODO code somewhat redundant to DavisDatagramClientDemo
  public static void of(StartAndStoppable davisEventProvider, DavisDecoder davisDecoder, DavisDevice davisDevice, double speed) {
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addDvsListener(davisEventStatistics);
    davisDecoder.addSigListener(davisEventStatistics);
    davisDecoder.addImuListener(davisEventStatistics);
    // ---
    AbstractAccumulatedImage abstractAccumulatedImage = AccumulatedEventsGrayImage.of(davisDevice);
    abstractAccumulatedImage.setInterval(50_000);
    // ---
    DavisViewerFrame davisViewerFrame = new DavisViewerFrame(Davis240c.INSTANCE, abstractAccumulatedImage);
    davisViewerFrame.setStatistics(davisEventStatistics);
    // handle dvs
    davisDecoder.addDvsListener(abstractAccumulatedImage);
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
