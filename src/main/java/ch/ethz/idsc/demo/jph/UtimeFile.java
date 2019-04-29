// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

import lcm.logging.Log;
import lcm.logging.Log.Event;

enum UtimeFile {
  ;
  public static void main(String[] args) throws IOException {
    File file = null;
    // 1554712556945803
    // 1554713015151247
    // file = DatahakiLogFileLocator.file(GokartLogFile._20190408T103556_eb6eadfe);
    // 1554714252982721
    // file = new File("/media/datahaki/data/gokart/ensemblelaps/dynamiclaps", "m00.lcm");
    // 1554714897564453
    // 1554715173041897
    // file = DatahakiLogFileLocator.file(GokartLogFile._20190401T115537_411917b6);
    file = new File("/media/datahaki/mobile/20190408", "DynamicKinematic.lcm.00");
    Log log = new Log(file.toString(), "r");
    Long firstt = null;
    Long maxtic = null;
    try {
      while (true) {
        Event event = log.readNext();
        boolean first = Objects.isNull(maxtic);
        if (first) {
          System.out.println(event.utime);
          firstt = event.utime;
        }
        maxtic = first //
            ? event.utime
            : Math.max(maxtic, event.utime);
        if (maxtic < 0)
          break;
      }
    } catch (Exception exception) {
      if (!(exception instanceof EOFException))
        exception.printStackTrace();
    }
    System.out.println(maxtic);
    Date date = new Date(firstt / 1000);
    System.out.println(new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(date));
  }
}
