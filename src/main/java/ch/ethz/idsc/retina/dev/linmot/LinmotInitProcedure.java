// code by nisaak, rvmoos, and jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.PriorityQueue;
import java.util.Queue;

public class LinmotInitProcedure {
  public final Queue<TimedPutEvent<LinmotPutEvent>> list = new PriorityQueue<>();

  public LinmotInitProcedure() {
    long timestamp = System.currentTimeMillis();
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutConfiguration.CMD_ERR_ACK, //
          LinmotPutConfiguration.MC_ZEROS);
      timestamp += 200;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutConfiguration.CMD_OFF_MODE, //
          LinmotPutConfiguration.MC_ZEROS);
      timestamp += 200;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutConfiguration.CMD_HOME, //
          LinmotPutConfiguration.MC_ZEROS);
      timestamp += 4000;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutConfiguration.CMD_OPERATION, //
          LinmotPutConfiguration.MC_ZEROS); //
      timestamp += 200;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
  }
}
