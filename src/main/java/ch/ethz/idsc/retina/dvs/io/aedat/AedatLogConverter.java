// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.File;
import java.util.Arrays;

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
    directory.mkdir();
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

  /** for use as a command line tool
   * 
   * @param args
   * @throws Exception */
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println(AedatLogConverter.class.getName());
      System.out.println("specify two arguments: source_aedat_file target_directory");
      System.out.println("your relative path is: " + new File("").getAbsolutePath());
    } else {
      Arrays.asList(args).forEach(System.out::println);
      System.out.println("please wait...");
      of(new File(args[0]), new File(args[1]));
    }
  }
}
