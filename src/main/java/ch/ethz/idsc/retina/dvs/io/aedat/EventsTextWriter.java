// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEventListener;

/** lists the events in a text file */
public class EventsTextWriter implements DvsDavisEventListener, AutoCloseable {
  private final BufferedWriter bufferedWriter;

  public EventsTextWriter(File directory) throws Exception {
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "events.txt")));
  }

  @Override
  public void dvs(DvsDavisEvent dvsDavisEvent) {
    try {
      bufferedWriter.write(String.format("%.6f %d %d %d\n", //
          dvsDavisEvent.time * 1e-6, //
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
