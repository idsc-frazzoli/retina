// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import lcm.logging.Log.Event;

class LinmotStatusTracker implements OfflineLogListener {
  boolean status = false;
  Scalar last = RealScalar.ZERO;

  @Override
  public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
    // System.out.println(event.channel);
    if (event.channel.equals("autobox.linmot.get")) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      if (status != linmotGetEvent.isOperational()) {
        status = linmotGetEvent.isOperational();
        System.out.println(time.number().doubleValue() + " " + status);
      }
    }
    last = time;
  }
}

/** analysis shows that status of brake may toggle
 * TODO the circumstances still have to be narrowed down */
enum LinmotStatusAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    File file;
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T105400_9e1d3699.lcm.00");
    file = new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00"); // <- status goes to false!
    LinmotStatusTracker offlineLogListener = new LinmotStatusTracker();
    OfflineLogPlayer.process(file, offlineLogListener);
    System.out.println(offlineLogListener.last.number().doubleValue());
  }
}
