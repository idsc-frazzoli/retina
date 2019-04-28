// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import lcm.logging.Log;
import lcm.logging.Log.Event;

enum UtimeFile {
  ;
  public static void main(String[] args) throws IOException {
    File file = null;
    // 1554712556945803
    // 1554713015151247
    file = DatahakiLogFileLocator.file(GokartLogFile._20190408T103556_eb6eadfe);
    // 1554714252982721
    file = new File("/media/datahaki/data/gokart/ensemblelaps/dynamiclaps", "m00.lcm");
    // 1554714897564453
    // 1554715173041897
    file = DatahakiLogFileLocator.file(GokartLogFile._20190408T111457_eb6eadfe);
    Log log = new Log(file.toString(), "r");
    Long maxtic = null;
    try {
      while (true) {
        Event event = log.readNext();
        boolean first = Objects.isNull(maxtic);
        if (first)
          System.out.println(event.utime);
        maxtic = first //
            ? event.utime
            : Math.max(maxtic, event.utime);
        if (maxtic < 0)
          break;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    System.out.println(maxtic);
  }
}
