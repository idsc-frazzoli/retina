// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.dvs.app.AccumulateDvsImage;
import ch.ethz.idsc.retina.dvs.app.DefaultDavisDisplay;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSupplier;

enum DavisViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    final File file1 = new File("/tmp", "DAVIS240C-2017-08-03T16-55-01+0200-02460045-0.aedat");
    final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    final File file3 = new File("/tmp", "DAVIS240C-2017-08-04T10-13-29+0200-02460045-0.aedat");
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    AedatFileSupplier aedatFileSupplier = new AedatFileSupplier(file1, davisDecoder);
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
    aedatFileSupplier.start();
    aedatFileSupplier.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
