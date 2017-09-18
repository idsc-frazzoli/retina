// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.retina.dev.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.dev.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.dev.davis.app.FirstImageTriggerExportControl;
import ch.ethz.idsc.retina.dev.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.dev.davis.io.DavisEventsTextWriter;
import ch.ethz.idsc.retina.dev.davis.io.DavisPngImageWriter;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.io.UserHome;
import idsc.BinaryBlob;
import idsc.DavisImu;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public class DavisLcmLogUzhConvert {
  public static void of(final File file, final File target) {
    DavisLcmClient davisLcmClient = new DavisLcmClient(null);
    final File directory = new File(target, file.getName());
    directory.mkdir();
    GlobalAssert.that(directory.isDirectory());
    FirstImageTriggerExportControl fitec = new FirstImageTriggerExportControl();
    long count = 0;
    try {
      DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
      davisLcmClient.davisDvsDatagramDecoder.addDvsListener(davisEventStatistics);
      // davisLcmClient.davisSigDatagramDecoder.addListener(davisEventStatistics);
      // davisLcmClient.davisDvsDatagramDecoder.addImuListener(davisEventStatistics);
      DavisEventsTextWriter eventsTextWriter = new DavisEventsTextWriter(directory, fitec);
      davisLcmClient.davisDvsDatagramDecoder.addDvsListener(eventsTextWriter);
      // ---
      DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
      davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
      // ---
      DavisPngImageWriter davisPngImageWriter = new DavisPngImageWriter(directory, fitec);
      SignalResetDifference signalResetDifference = new SignalResetDifference(davisImageBuffer);
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
          // TODO magic const extensions
          if (event.channel.endsWith(".imu")) // imu
            davisLcmClient.digestImu(new DavisImu(event.data));
          if (event.channel.endsWith(".sig")) // signal aps
            davisLcmClient.digestSig(new BinaryBlob(event.data));
          if (event.channel.endsWith(".rst")) // reset read aps
            davisLcmClient.digestRst(new BinaryBlob(event.data));
          if (event.channel.endsWith(".dvs")) // events
            davisLcmClient.digestDvs(new BinaryBlob(event.data));
        }
      } catch (IOException exception) {
        // ---
      }
      eventsTextWriter.close();
      davisPngImageWriter.close();
      davisEventStatistics.print();
      System.out.println("total_frames" + davisPngImageWriter.total_frames());
    } catch (IOException exception) {
      // ---
    }
    System.out.println("entries: " + count);
  }

  public static void main(String[] args) {
    File file = UserHome.file("lcm_20170907T170846_65df29fb.log.00");
    File target = new File("/media/datahaki/media/ethz/export");
    of(file, target);
  }
}
