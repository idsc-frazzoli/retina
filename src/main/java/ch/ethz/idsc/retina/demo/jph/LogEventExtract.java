// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

// TODO make this a function!
enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    File src = new File("/media/datahaki/mobile/temp", "20180108T162528_5f742add.lcm.00");
    File dst = new File("/home/datahaki", "20180108T162528_5f742add.lcm.00.extract");
    int lo = 832195;
    int hi = 2856393;
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
