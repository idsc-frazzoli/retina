// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.davis.io.Aedat20FileSupplier;
import ch.ethz.idsc.retina.davis.io.DavisEventsTextWriter;
import ch.ethz.idsc.retina.davis.io.DavisPngImageWriter;
import ch.ethz.idsc.retina.davis.io.DavisSimpleImageWriter;

/** functionality is available as a command-line tool */
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
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    Aedat20FileSupplier aedatFileSupplier = new Aedat20FileSupplier(aedat, davisDecoder);
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addDvsListener(davisEventStatistics);
    davisDecoder.addSigListener(davisEventStatistics);
    davisDecoder.addImuListener(davisEventStatistics);
    // ---
    FirstImageTriggerExportControl fitec = new FirstImageTriggerExportControl();
    try (DavisEventsTextWriter eventsTextWriter = new DavisEventsTextWriter(directory, fitec)) {
      davisDecoder.addDvsListener(eventsTextWriter);
      // ---
      DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
      try (DavisPngImageWriter pngImageWriter = new DavisPngImageWriter(directory, fitec)) {
        davisImageProvider.addListener(fitec);
        davisImageProvider.addListener(pngImageWriter);
        davisDecoder.addSigListener(davisImageProvider);
        // ---
        AbstractAccumulatedImage accumulateDvsImage = AccumulatedEventsGrayImage.of(davisDevice);
        accumulateDvsImage.setInterval(20_000);
        {
          File debug = new File(directory, "events_debug");
          debug.mkdir();
          accumulateDvsImage.addListener(new DavisSimpleImageWriter(debug, 50, fitec));
          davisDecoder.addDvsListener(accumulateDvsImage);
        }
        // ---
        aedatFileSupplier.start();
        aedatFileSupplier.stop();
      }
    }
    davisEventStatistics.print();
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
