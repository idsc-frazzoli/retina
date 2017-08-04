// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.ethz.idsc.retina.dev.davis240c.ApsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.DavisEventListener;
import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.ImuDavisEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Round;

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
    // TODO doesn't have to use double but do string manipulation
    Scalar scalar = Round._6.apply(DoubleScalar.of(dvsDavisEvent.time * 1e-6));
    try {
      bufferedWriter.write(String.format("%s000 %d %d %d\n", //
          scalar.toString(), dvsDavisEvent.x, dvsDavisEvent.y, dvsDavisEvent.i));
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
