// code by jph
package ch.ethz.idsc.demo.yt;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.MessageConsistency;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    File src = new File("/media/datahaki/media/ethz/gokartlogs", "20180226T150533_ed1c7f0a.lcm.00");
    File dst = null;
    dst = new File("/home/datahaki/interesting.lcm");
    if (dst.exists()) {
      System.out.println("deleting: " + dst);
      dst.delete();
    }
    int lo = 1178000;
    int hi = 1206000;
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      // int count = 0;
      while (true) {
        Event event = log.readNext();
        if (lo <= event.eventNumber && event.eventNumber < hi) {
          try {
            new BinaryBlob(event.data);
            logWriter.write(event);
          } catch (Exception exception) {
            // ---
            exception.printStackTrace();
          }
        }
      }
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      // ---
    }
    logWriter.close();
    // ---
    OfflineLogPlayer.process(dst, MessageConsistency.INSTANCE);
  }
}
