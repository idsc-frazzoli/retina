// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.lcm.MessageConsistency;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    File src = DatahakiLogFileLocator.file(GokartLogFile._20180705T101944_b01c2886);
    final File folder = new File("/media/datahaki/media/ethz/gokart/topic/track_white", //
        "20180705T101944_2");
    folder.mkdir();
    if (!folder.isDirectory())
      throw new RuntimeException();
    new File(folder, "GokartLogConfig.properties").createNewFile();
    File dst = new File(folder, "log.lcm");
    if (dst.exists()) {
      System.out.println("deleting: " + dst);
      dst.delete();
    }
    int lo = 3715260;
    int hi = 3986070;
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
