// code by jph
package ch.ethz.idsc.retina.davis.io.txt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.ethz.idsc.retina.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.davis.io.DavisExportControl;

/** lists the events in a text file */
public class DavisEventsTextWriter implements DavisDvsEventListener, AutoCloseable {
  private final BufferedWriter bufferedWriter;
  private final DavisExportControl exportControl;

  public DavisEventsTextWriter(File directory, DavisExportControl exportControl) throws Exception {
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "events.txt")));
    this.exportControl = exportControl;
  }

  @Override
  public void dvs(DavisDvsEvent dvsDavisEvent) {
    if (exportControl.isActive())
      try {
        int mapped = exportControl.mapTime(dvsDavisEvent.time);
        bufferedWriter.write(String.format("%.6f %d %d %d\n", //
            mapped * 1e-6, //
            dvsDavisEvent.x, //
            dvsDavisEvent.y, //
            dvsDavisEvent.i));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }
}
