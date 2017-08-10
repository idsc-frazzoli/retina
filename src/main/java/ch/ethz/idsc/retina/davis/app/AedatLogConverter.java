// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDevice;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis._240c.DavisImageProvider;
import ch.ethz.idsc.retina.davis.io.aedat.AedatFileSupplier;
import ch.ethz.idsc.retina.davis.io.png.PngImageWriter;
import ch.ethz.idsc.retina.davis.io.png.SimpleImageWriter;
import ch.ethz.idsc.retina.davis.io.txt.EventsTextWriter;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** functionality is available as a command-line tool */
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
    DavisDevice davisDevice = Davis240c.INSTANCE;
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    AedatFileSupplier aedatFileSupplier = new AedatFileSupplier(aedat, davisDecoder);
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addListener(davisEventStatistics);
    // ---
    FirstImageTriggerExportControl fitec = new FirstImageTriggerExportControl();
    EventsTextWriter eventsTextWriter = new EventsTextWriter(directory, fitec);
    davisDecoder.addListener(eventsTextWriter);
    // ---
    DavisImageProvider davisImageProvider = new DavisImageProvider(davisDevice);
    PngImageWriter pngImageWriter = new PngImageWriter(directory, fitec);
    davisImageProvider.addListener(fitec);
    davisImageProvider.addListener(pngImageWriter);
    davisDecoder.addListener(davisImageProvider);
    // ---
    AccumulatedEventsImage accumulateDvsImage = new AccumulatedEventsImage(davisDevice, 20000);
    {
      File debug = new File(directory, "events_debug");
      debug.mkdir();
      accumulateDvsImage.addListener(new SimpleImageWriter(debug, 50, fitec));
      davisDecoder.addListener(accumulateDvsImage);
    }
    // ---
    aedatFileSupplier.start();
    aedatFileSupplier.stop();
    eventsTextWriter.close();
    pngImageWriter.close();
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
