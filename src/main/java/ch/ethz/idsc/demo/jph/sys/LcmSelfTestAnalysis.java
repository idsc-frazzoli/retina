// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.demo.GokartLogFile;
import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;

/* package */ enum LcmSelfTestAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    GokartLogFile gokartLogFile = GokartLogFile._20190215T152744_380160a9;
    File file = DatahakiLogFileLocator.file(gokartLogFile);
    try (Log log = new Log(file.toString(), "r")) {
      Long tic = null;
      while (true) {
        Event event = log.readNext();
        if (Objects.isNull(tic))
          tic = event.utime;
        try {
          new BinaryBlob(event.data);
        } catch (Exception exception) {
          System.err.println(event.channel);
          System.out.println(event.data.length);
          // lcm s.elf t.est
          System.out.println(new String(event.data));
          exception.printStackTrace();
        }
      }
    }
  }
}
