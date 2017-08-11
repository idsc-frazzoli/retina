// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AccumulatedEventsImage;
import ch.ethz.idsc.retina.davis.app.DavisDefaultDisplay;
import ch.ethz.idsc.retina.davis.io.aps.ApsDatagramClient;
import ch.ethz.idsc.retina.davis.io.dvs.DvsDatagramClient;

enum DavisDatagramClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    DavisDefaultDisplay davisImageDisplay = new DavisDefaultDisplay();
    // subscribe to dvs events:
    DvsDatagramClient dvsDatagramClient = new DvsDatagramClient(davisDecoder);
    AccumulatedEventsImage accumulateDvsImage = new AccumulatedEventsImage(Davis240c.INSTANCE, 20_000);
    dvsDatagramClient.addListener(accumulateDvsImage);
    accumulateDvsImage.addListener(davisImageDisplay);
    // subscribe to aps events:
    ApsDatagramClient apsDatagramClient = new ApsDatagramClient(davisDecoder);
    apsDatagramClient.addListener(davisImageDisplay);
    // ---
    new Thread(() -> {
      dvsDatagramClient.start();
    }).start();
    apsDatagramClient.start(); // TODO at the moment this is a blocking call !?
    System.out.println("here");
  }
}
