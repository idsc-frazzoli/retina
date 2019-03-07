// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameCollector;
import ch.ethz.idsc.retina.davis.io.Aedat20FileSupplier;

/** functionality is available as a command-line tool */
public enum AedatLogStatistics {
  ;
  /** @param aedat file as source
   * @param directory target
   * @throws Exception */
  public static void of(File aedat) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    Aedat20FileSupplier aedatFileSupplier = new Aedat20FileSupplier(aedat, davisDecoder);
    // ---
    DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
    davisDecoder.addDvsListener(davisEventStatistics);
    davisDecoder.addSigListener(davisEventStatistics);
    davisDecoder.addImuListener(davisEventStatistics);
    DavisImuFrameCollector davisImuProvider = new DavisImuFrameCollector();
    davisDecoder.addImuListener(davisImuProvider);
    // ---
    aedatFileSupplier.start();
    aedatFileSupplier.stop();
    davisEventStatistics.print();
  }

  /** for use as a command line tool
   * 
   * @param args
   * @throws Exception */
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println(AedatLogStatistics.class.getName());
      System.out.println("specify one arguments: source_aedat_file");
      System.out.println("your relative path is: " + new File("").getAbsolutePath());
    } else {
      Arrays.asList(args).forEach(System.out::println);
      System.out.println("please wait...");
      of(new File(args[0]));
    }
  }
}
