// code by jph
package ch.ethz.idsc.demo.ni;

import java.io.File;

import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    File src = new File("C:\\Users\\maste_000\\Documents\\ETH\\LogFilesKart\\1218", "20171218T121006_9b56b71b.lcm.00");
    File dst = new File("C:\\Users\\maste_000\\Documents\\ETH\\LogFilesKart\\1218", "20171218T121006_9b56b71b.lcm.00.extract");
    int lo = 2033209;
    int hi = 2951855;
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      while (true) {
        Event event = log.readNext();
        if (lo < event.eventNumber && event.eventNumber < hi) {
          logWriter.write(event);
        }
      }
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      // ---
    }
    logWriter.close();
  }
}
