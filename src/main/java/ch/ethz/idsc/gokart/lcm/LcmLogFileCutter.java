// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.NavigableMap;

import idsc.BinaryBlob;
import lcm.logging.Log;
import lcm.logging.Log.Event;
import lcm.logging.LogEventWriter;

public abstract class LcmLogFileCutter {
  /** @param src
   * @param navigableMap with entries that encode start and end
   * of segments that should be extracted to separate files
   * @throws IOException */
  public LcmLogFileCutter(File src, NavigableMap<Integer, Integer> navigableMap) throws IOException {
    final Log log = new Log(src.toString(), "r");
    int index = 0;
    for (Entry<Integer, Integer> entry : navigableMap.entrySet()) {
      System.out.println(entry);
      final int lo = entry.getKey();
      final int hi = entry.getValue();
      File dst = filename(index);
      if (dst.exists()) {
        System.out.println("deleting: " + dst);
        dst.delete();
      }
      LogEventWriter logWriter = new LogEventWriter(dst);
      {
        while (true) {
          Event event = log.readNext();
          if (lo <= event.eventNumber + 1)
            break;
        }
        while (true) {
          Event event = log.readNext();
          try {
            new BinaryBlob(event.data);
            logWriter.write(event);
          } catch (Exception exception) {
            // ---
            exception.printStackTrace();
          }
          if (hi <= event.eventNumber)
            break;
        }
      }
      logWriter.close();
      ++index;
    }
  }

  /** @param index
   * @return file to which extract entry with given index */
  public abstract File filename(int index);
}
