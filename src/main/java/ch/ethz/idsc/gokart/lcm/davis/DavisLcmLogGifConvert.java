// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis._240c.DavisEventStatistics;
import ch.ethz.idsc.retina.davis.app.AccumulatedOverlay;
import ch.ethz.idsc.retina.davis.app.DavisImageBuffer;
import ch.ethz.idsc.retina.davis.app.FirstImageTriggerExportControl;
import ch.ethz.idsc.retina.davis.app.SignalResetDifference;
import ch.ethz.idsc.retina.davis.io.DavisGifImageWriter;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

public class DavisLcmLogGifConvert {
  public static void of(final File file, final File target) {
    DavisLcmClient davisLcmClient = new DavisLcmClient(null);
    FirstImageTriggerExportControl fitec = new FirstImageTriggerExportControl();
    long count = 0;
    try {
      DavisEventStatistics davisEventStatistics = new DavisEventStatistics();
      davisLcmClient.addDvsListener(davisEventStatistics);
      // ---
      AccumulatedOverlay accumulatedOverlay = new AccumulatedOverlay(Davis240c.INSTANCE, 3000);
      // ---
      final DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
      davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
      // davisLcmClient.davisRstDatagramDecoder.addListener(accumulatedOverlay.rst);
      // ---
      try (DavisGifImageWriter davisGifImageWriter = //
          new DavisGifImageWriter(new File(target, file.getName() + ".gif"), 100, fitec)) {
        SignalResetDifference signalResetDifference = SignalResetDifference.normal(davisImageBuffer);
        davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
        davisLcmClient.davisSigDatagramDecoder.addListener(fitec);
        // ---
        davisLcmClient.addDvsListener(accumulatedOverlay);
        signalResetDifference.addListener(accumulatedOverlay.differenceListener);
        accumulatedOverlay.addListener(davisGifImageWriter);
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
        System.out.println("total_frames" + davisGifImageWriter.total_frames());
      }
      davisEventStatistics.print();
    } catch (IOException exception) {
      // ---
    }
    System.out.println("entries: " + count);
  }
}
