// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.dvs.app.DavisImageDisplay;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSupplier;

enum DavisViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    final File file1 = new File("/tmp", "DAVIS240C-2017-08-03T16-55-01+0200-02460045-0.aedat");
    final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    final File file3 = new File("/tmp", "DAVIS240C-2017-08-04T10-13-29+0200-02460045-0.aedat");
    AedatFileSupplier aedatFileSupplier = new AedatFileSupplier(file3, Davis240c.INSTANCE, Davis240c.INSTANCE);
    // ---
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    aedatFileSupplier.addListener(davisEventStatistics);
    DavisImageDisplay davisImageDisplay = new DavisImageDisplay();
    DavisImageProvider davisImageProvider = new DavisImageProvider(Davis240c.INSTANCE);
    davisImageProvider.addListener(davisImageDisplay);
    aedatFileSupplier.addListener(davisImageProvider);
    // ---
    aedatFileSupplier.start();
    aedatFileSupplier.stop();
    davisEventStatistics.print();
    davisImageDisplay.close();
  }
}
