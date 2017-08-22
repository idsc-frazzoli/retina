// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.dev.davis.app.DavisViewerFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsDatagramClient;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramClient;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuDatagramClient;

enum DavisDatagramClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisDecoder davisDecoder = davisDevice.createDecoder();
    DavisViewerFrame davisImageDisplay = new DavisViewerFrame(davisDevice);
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addDvsListener(davisEventStatistics);
    davisDecoder.addSigListener(davisEventStatistics);
    davisDecoder.addImuListener(davisEventStatistics);
    davisImageDisplay.setStatistics(davisEventStatistics);
    // subscribe to dvs events:
    DavisDvsDatagramClient dvsDatagramClient = new DavisDvsDatagramClient();
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(Davis240c.INSTANCE, 20_000);
    dvsDatagramClient.davisDvsDatagramDecoder.addListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisImageDisplay);
    // subscribe to aps events:
    DavisApsDatagramClient apsDatagramClient = new DavisApsDatagramClient();
    apsDatagramClient.davisApsDatagramDecoder.addListener(davisImageDisplay);
    // subscribe to imu events:
    DavisImuDatagramClient imuDatagramClient = new DavisImuDatagramClient();
    imuDatagramClient.addListener(davisImageDisplay);
    // ---
    new Thread(() -> {
      dvsDatagramClient.start();
    }).start();
    new Thread(() -> {
      imuDatagramClient.start();
    }).start();
    apsDatagramClient.start(); // TODO at the moment this is a blocking call !?
    System.out.println("here");
  }
}
