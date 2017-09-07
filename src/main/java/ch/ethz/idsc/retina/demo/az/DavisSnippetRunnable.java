// code by jph
package ch.ethz.idsc.retina.demo.az;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.retina.dev.davis.app.FirstImageTriggerExportControl;
import ch.ethz.idsc.retina.dev.davis.io.DavisEventsTextWriter;
import ch.ethz.idsc.retina.dev.davis.io.DavisPngImageWriter;
import ch.ethz.idsc.retina.lcm.LcmLogProcess;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.GlobalAssert;
import idsc.BinaryBlob;
import idsc.DavisImu;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public abstract class DavisSnippetRunnable implements Runnable {
  private final int milliSeconds;

  public DavisSnippetRunnable(int milliSeconds) {
    this.milliSeconds = milliSeconds;
  }

  @Override
  public void run() {
    try {
      LcmLogProcess lcmLogProcess = LcmLogProcess.createDefault();
      File file = lcmLogProcess.file();
      System.out.println(file);
      Thread.sleep(milliSeconds);
      lcmLogProcess.close();
      extractImagesEtc(file, new File("/media/datahaki/media/ethz/export")); // FIXME
      callback();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public abstract void callback();

  private static void extractImagesEtc(final File file, final File target) {
    DavisLcmClient davisLcmClient = new DavisLcmClient(null);
    final File directory = new File(target, file.getName());
    directory.mkdir();
    GlobalAssert.that(directory.isDirectory());
    FirstImageTriggerExportControl fitec = new FirstImageTriggerExportControl();
    long count = 0;
    try {
      DavisEventsTextWriter eventsTextWriter = new DavisEventsTextWriter(directory, fitec);
      davisLcmClient.davisDvsDatagramDecoder.addDvsListener(eventsTextWriter);
      // ---
      DavisPngImageWriter davisPngImageWriter = new DavisPngImageWriter(directory, fitec);
      davisLcmClient.davisSigDatagramDecoder.addListener(davisPngImageWriter);
      davisLcmClient.davisSigDatagramDecoder.addListener(fitec);
      // ---
      // ResetDavisApsCorrection resetDavisApsCorrection = new ResetDavisApsCorrection();
      // davisLcmClient.davisRstDatagramDecoder.addListener(resetDavisApsCorrection);
      Log log = new Log(file.toString() + ".00", "r"); // TODO not generic
      Set<String> set = new HashSet<>();
      try {
        while (true) {
          Event event = log.readNext();
          ++count;
          if (set.add(event.channel))
            System.out.println(event.channel);
          if (event.channel.endsWith(".imu"))
            davisLcmClient.digestImu(new DavisImu(event.data));
          if (event.channel.endsWith(".sig"))
            davisLcmClient.digestSig(new BinaryBlob(event.data));
          if (event.channel.endsWith(".rst"))
            davisLcmClient.digestRst(new BinaryBlob(event.data));
          if (event.channel.endsWith(".dvs"))
            davisLcmClient.digestDvs(new BinaryBlob(event.data));
        }
      } catch (IOException exception) {
        // ---
      }
      eventsTextWriter.close();
      davisPngImageWriter.close();
    } catch (IOException exception) {
      // ---
    }
    System.out.println("entries: " + count);
  }
}
