// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.davis.app.DavisDefaultDisplay;
import ch.ethz.idsc.retina.davis.io.aps.ApsDatagramClient;
import ch.ethz.idsc.retina.davis.io.dvs.DvsDatagramClient;
import ch.ethz.idsc.retina.davis.io.imu.ImuDatagramClient;

enum DavisDatagramClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    DavisDefaultDisplay davisImageDisplay = new DavisDefaultDisplay();
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addListener(davisEventStatistics);
    davisImageDisplay.setStatistics(davisEventStatistics);
    // subscribe to dvs events:
    DvsDatagramClient dvsDatagramClient = new DvsDatagramClient();
    AccumulatedEventsImage accumulatedEventsImage = new AccumulatedEventsImage(Davis240c.INSTANCE, 20_000);
    dvsDatagramClient.dvsDatagramDecoder.addListener(accumulatedEventsImage);
    accumulatedEventsImage.addListener(davisImageDisplay);
    // subscribe to aps events:
    ApsDatagramClient apsDatagramClient = new ApsDatagramClient(davisDecoder);
    apsDatagramClient.addListener(davisImageDisplay);
    // subscribe to imu events:
    ImuDatagramClient imuDatagramClient = new ImuDatagramClient();
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
