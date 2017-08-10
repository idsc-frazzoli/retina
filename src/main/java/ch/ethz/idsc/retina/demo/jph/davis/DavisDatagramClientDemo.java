// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.AccumulateDvsImage;
import ch.ethz.idsc.retina.davis.app.DavisDefaultDisplay;
import ch.ethz.idsc.retina.davis.io.aps.ApsDatagramClient;
import ch.ethz.idsc.retina.davis.io.dvs.DvsDatagramClient;

enum DavisDatagramClientDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    DavisDefaultDisplay davisImageDisplay = new DavisDefaultDisplay();
    // subscribe to dvs events:
    DvsDatagramClient dvsStandaloneClient = new DvsDatagramClient(davisDecoder);
    AccumulateDvsImage accumulateDvsImage = new AccumulateDvsImage(Davis240c.INSTANCE, 50000);
    dvsStandaloneClient.addListener(accumulateDvsImage);
    accumulateDvsImage.addListener(davisImageDisplay.dvsRenderer);
    // subscribe to aps events:
    ApsDatagramClient apsDatagramClient = new ApsDatagramClient(davisDecoder);
    apsDatagramClient.addListener(davisImageDisplay.apsRenderer);
    // ---
    new Thread(() -> {
      dvsStandaloneClient.start();
    }).start();
    apsDatagramClient.start(); // TODO at the moment this is a blocking call !?
    System.out.println("here");
  }
}
