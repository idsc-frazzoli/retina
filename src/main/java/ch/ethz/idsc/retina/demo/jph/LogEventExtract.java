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
    File src = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00");
    // new File("/media/datahaki/mobile/temp", "20180108T162528_5f742add.lcm.00");
    File dst = new File("/home/datahaki/Projects/retina/src/test/resources/localization", "vlp16ray_rimoget.lcm");
    dst.delete();
    // int lo = 4778324;
    // int hi = 4809318;
    int lo = 4795786;
    // int hi = 4799921;
    int hi = 4796521;
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      while (true) {
        Event event = log.readNext();
        if (lo < event.eventNumber && event.eventNumber < hi) {
          if (event.channel.startsWith("vlp16.center.ray") || event.channel.equals("autobox.rimo.get")) {
            logWriter.write(event);
          }
        }
      }
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      // ---
    }
    logWriter.close();
  }
}
