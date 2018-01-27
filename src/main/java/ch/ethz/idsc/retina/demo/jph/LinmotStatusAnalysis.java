// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import lcm.logging.Log.Event;

class LinmotStatusTracker implements OfflineLogListener {
  boolean status = false;
  Scalar last = RealScalar.ZERO;
  int count = 0;
  TensorBuilder tensorBuilder = new TensorBuilder();

  @Override
  public void event(Scalar time, Event event, ByteBuffer byteBuffer) {
    // System.out.println(event.channel);
    if (event.channel.equals("autobox.linmot.get")) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      if (status != linmotGetEvent.isOperational()) {
        status = linmotGetEvent.isOperational();
        System.out.println(time.number().doubleValue() + " " + status);
      }
      tensorBuilder.append(Tensors.vector( //
          time.number().doubleValue(), //
          linmotGetEvent.status_word, //
          linmotGetEvent.state_variable, //
          linmotGetEvent.actual_position, //
          linmotGetEvent.demand_position, //
          linmotGetEvent.getWindingTemperature1().number().doubleValue(), //
          linmotGetEvent.getWindingTemperature2().number().doubleValue() //
      ));
      ++count;
      last = time;
    }
  }

  public Tensor getTensor() {
    return tensorBuilder.getTensor();
  }
}

/** analysis shows that status of brake may toggle
 * TODO the circumstances still have to be narrowed down */
enum LinmotStatusAnalysis {
  ;
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dhl : DubendorfHangarLog.values()) {
      File file = dhl.file(LOG_ROOT);
      if (file.isFile()) {
        System.out.println(dhl);
        // new File("/media/datahaki/media/ethz/gokartlogs", "20180112T113153_9e1d3699.lcm.00"); // <- status goes to false!
        LinmotStatusTracker linmotStatusTracker = new LinmotStatusTracker();
        OfflineLogPlayer.process(file, linmotStatusTracker);
        System.out.println(linmotStatusTracker.last.number().doubleValue());
        System.out.println(linmotStatusTracker.count);
        Export.of(UserHome.file(file.getName() + ".csv"), linmotStatusTracker.getTensor());
      }
    }
  }
}
