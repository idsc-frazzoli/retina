// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.ethz.idsc.retina.dev.davis240c.ApsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.DavisEventListener;
import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.ImuDavisEvent;

public class EventsTextWriter implements DavisEventListener, AutoCloseable {
  private final BufferedWriter bufferedWriter;

  public EventsTextWriter(File directory) throws Exception {
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "events.txt")));
  }

  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    // ---
  }

  @Override
  public void dvs(DvsDavisEvent dvsDavisEvent) {
    try {
      bufferedWriter.write(String.format("%.6f %d %d %d\n", //
          dvsDavisEvent.time * 1e-6, dvsDavisEvent.x, 179 - dvsDavisEvent.y, dvsDavisEvent.i));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
    // ---
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }
}
