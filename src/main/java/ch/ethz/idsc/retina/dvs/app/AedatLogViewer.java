// code by jph
package ch.ethz.idsc.retina.dvs.app;

import java.io.File;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.dev.davis._240c.EventRealtimeSleeper;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSupplier;

public enum AedatLogViewer {
  ;
  public static void of(File file, DavisDevice davisDevice) throws Exception {
    DavisDecoder davisDecoder = davisDevice.createDecoder();
    AedatFileSupplier aedatFileSupplier = new AedatFileSupplier(file, davisDecoder);
    // ---
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addListener(davisEventStatistics);
    DefaultDavisDisplay davisImageDisplay = new DefaultDavisDisplay();
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    davisImageProvider.addListener(davisImageDisplay.apsRenderer);
    davisDecoder.addListener(davisImageProvider);
    // ---
    AccumulateDvsImage accumulateDvsImage = new AccumulateDvsImage(davisDevice, 20000);
    davisDecoder.addListener(accumulateDvsImage);
    accumulateDvsImage.addListener(davisImageDisplay.dvsRenderer);
    // ---
    davisDecoder.addListener(new EventRealtimeSleeper());
    // ---
    aedatFileSupplier.start();
    aedatFileSupplier.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
