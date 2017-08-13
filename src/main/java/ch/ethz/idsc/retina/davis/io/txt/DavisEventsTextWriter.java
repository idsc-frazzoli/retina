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
  private final DavisExportControl davisExportControl;

  public DavisEventsTextWriter(File directory, DavisExportControl davisExportControl) throws Exception {
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "events.txt")));
    this.davisExportControl = davisExportControl;
  }

  @Override
  public void dvs(DavisDvsEvent davisDvsEvent) {
    if (davisExportControl.isActive())
      try {
        int mapped = davisExportControl.mapTime(davisDvsEvent.time);
        bufferedWriter.write(String.format("%.6f %d %d %d\n", //
            mapped * 1e-6, //
            davisDvsEvent.x, //
            davisDvsEvent.y, //
            davisDvsEvent.i));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }
}
