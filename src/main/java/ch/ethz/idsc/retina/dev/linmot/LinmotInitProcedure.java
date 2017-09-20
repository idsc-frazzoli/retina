package ch.ethz.idsc.retina.dev.linmot;

import java.util.PriorityQueue;
import java.util.Queue;

public class LinmotInitProcedure {
  public final Queue<TimedPutEvent<LinmotPutEvent>> list = new PriorityQueue<>();

  public LinmotInitProcedure() {
    long timestamp = System.currentTimeMillis();
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_ERR_ACK.getShort(); // Error Acknowledgment
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort();
      timestamp += 200;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_OFF_MODE.getShort(); // Off Mode
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort();
      timestamp += 200;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_HOME.getShort(); // Home
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort();
      timestamp += 4000;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_OPERATION.getShort(); // Operation
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort(); // Position
      timestamp += 200;
      list.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
  }
}
