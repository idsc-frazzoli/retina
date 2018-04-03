// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.demo.jph.MessageConsistency;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    // File src = new File("home/mario/"+LogfileLocations.DUBI1);
    File src = UserHome.file(LogfileLocations.DUBI4);
    System.out.println(src.toString());
    File dst = null;
    dst = new File("/home/mario/logs/20180307T154859_0cd18c6bExtracted.lcm");
    if (dst.exists()) {
      System.out.println("deleting: " + dst);
      dst.delete();
    }
    int lo = 1755000;
    int hi = 1859000;
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
