// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.File;

import ch.ethz.idsc.retina.dev.davis240c.DavisImageProvider;
import ch.ethz.idsc.retina.util.data.GlobalAssert;

// TODO class name not final
public enum AedatLogConverter {
  ;
  /** @param aedat file as source
   * @param directory target
   * @throws Exception */
  public static void of(File aedat, File directory) throws Exception {
    GlobalAssert.that(aedat.isFile());
    GlobalAssert.that(directory.isDirectory());
    // ---
    AedatFileSupplier aedatFileSupplier = new AedatFileSupplier(aedat);
    // ---
    EventsTextWriter eventsTextWriter = new EventsTextWriter(directory);
    aedatFileSupplier.addListener(eventsTextWriter);
    // ---
    DavisImageProvider davisImageProvider = new DavisImageProvider();
    PngImageWriter pngImageWriter = new PngImageWriter(directory);
    davisImageProvider.addListener(pngImageWriter);
    aedatFileSupplier.addListener(davisImageProvider);
    // ---
    aedatFileSupplier.start();
    aedatFileSupplier.stop();
    eventsTextWriter.close();
    pngImageWriter.close();
  }

  // TODO remove later!
  public static void main(String[] args) throws Exception {
    final File file1 = new File("/tmp", "DAVIS240C-2017-08-03T16-55-01+0200-02460045-0.aedat");
    final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    // File file = file1;
    of(file1, new File("/media/datahaki/media/ethz/davis240c/rec1"));
  }
}
