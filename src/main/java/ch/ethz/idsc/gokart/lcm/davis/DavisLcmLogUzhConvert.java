// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.davis.app.FirstImageTriggerExportControl;
import ch.ethz.idsc.retina.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.davis.io.DavisEventsTextWriter;
import ch.ethz.idsc.retina.davis.io.DavisPngImageWriter;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public enum DavisLcmLogUzhConvert {
  ;
  public static void of(final File file, final File target) {
    DavisLcmClient davisLcmClient = new DavisLcmClient(null);
    final File directory = new File(target, file.getName());
    directory.mkdir();
    GlobalAssert.that(directory.isDirectory());
    FirstImageTriggerExportControl fitec = new FirstImageTriggerExportControl();
    long count = 0;
    try {
      DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
      davisLcmClient.addDvsListener(davisEventStatistics);
      // davisLcmClient.davisSigDatagramDecoder.addListener(davisEventStatistics);
      // davisLcmClient.davisDvsDatagramDecoder.addImuListener(davisEventStatistics);
      try (DavisEventsTextWriter eventsTextWriter = new DavisEventsTextWriter(directory, fitec)) {
        davisLcmClient.addDvsListener(eventsTextWriter);
        // ---
        DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
        davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
        // ---
        try (DavisPngImageWriter davisPngImageWriter = new DavisPngImageWriter(directory, fitec)) {
          SignalResetDifference signalResetDifference = SignalResetDifference.normal(davisImageBuffer);
          signalResetDifference.addListener(davisPngImageWriter);
          davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
          davisLcmClient.davisSigDatagramDecoder.addListener(fitec);
          // ---
          // AccumulatedEventsImage accumulateDvsImage = new
          // AccumulatedEventsImage(Davis240c.INSTANCE, 20000);
          // {
          // File debug = new File(directory, "events_debug");
          // debug.mkdir();
          // accumulateDvsImage.addListener(new DavisSimpleImageWriter(debug, 50, fitec));
          // davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulateDvsImage);
          // }
          // ---
          Log log = new Log(file.toString(), "r");
          Set<String> set = new HashSet<>();
          try {
            while (true) {
              Event event = log.readNext();
              ++count;
              if (set.add(event.channel))
                System.out.println(event.channel);
              if (event.channel.endsWith(DavisLcmChannel.SIG.extension)) // signal aps
                davisLcmClient.digestSig(new BinaryBlob(event.data));
              if (event.channel.endsWith(DavisLcmChannel.RST.extension)) // reset read aps
                davisLcmClient.digestRst(new BinaryBlob(event.data));
              if (event.channel.endsWith(DavisLcmChannel.DVS.extension)) // events
                davisLcmClient.digestDvs(new BinaryBlob(event.data));
            }
          } catch (IOException exception) {
            // ---
          }
          System.out.println("total_frames" + davisPngImageWriter.total_frames());
        }
      }
      davisEventStatistics.print();
    } catch (IOException exception) {
      // ---
    }
    System.out.println("entries: " + count);
  }
}
