// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.subare.util.UserHome;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

// TODO make this a function!
enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    File src = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00");
    // new File("/media/datahaki/mobile/temp", "20180108T162528_5f742add.lcm.00");
    File dst = UserHome.file("some.lcm");
    // new File("/home/datahaki/Projects/retina/src/test/resources/localization", "Xvlp16.center.pos.lcm");
    dst.delete();
    // int lo = 4795786;
    // int hi = 4796521;
    int lo = 4781786;
    int hi = 4829921;
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      int count = 0;
      while (true) {
        Event event = log.readNext();
        if (lo < event.eventNumber && event.eventNumber < hi) {
          // if (event.channel.startsWith("vlp16.center.ray") || event.channel.equals("autobox.rimo.get"))
          if (event.channel.startsWith("vlp16.center.pos")) {
            if (count % 140 == 0) {
              System.out.println("here");
              logWriter.write(event);
            }
            ++count;
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
