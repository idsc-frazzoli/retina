// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.subare.util.UserHome;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

// TODO make this a function!
enum LogEventExtract {
  ;
  public static void main(String[] args) throws Exception {
    File src = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00");
    src = DubendorfHangarLog._20171213T162832_55710a6b.file(GokartLcmLogPlayer.LOG_ROOT);
    // new File("/media/datahaki/mobile/temp", "20180108T162528_5f742add.lcm.00");
    File dst = UserHome.file("20180108T165210_maxtorque.lcm");
    // new File("/home/datahaki/Projects/retina/src/test/resources/localization", "Xvlp16.center.pos.lcm");
    dst = UserHome.file("temp/20171213T162832_brake5.lcm");
    dst.delete();
    int lo = 1116659;
    int hi = 1132803;
    // ---
    Log log = new Log(src.toString(), "r");
    LogEventWriter logWriter = new LogEventWriter(dst);
    try {
      // int count = 0;
      while (true) {
        Event event = log.readNext();
        if (lo < event.eventNumber && event.eventNumber < hi)
          logWriter.write(event);
      }
    } catch (Exception exception) {
      System.err.println(exception.getMessage());
      // ---
    }
    logWriter.close();
  }
}
