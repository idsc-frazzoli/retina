package ch.ethz.idsc.retina.dev.linmot;

import java.util.PriorityQueue;
import java.util.Queue;

public class LinmotInitProcedure {
  public final Queue<TimedLinmotPutEvent> list = new PriorityQueue<>();

  public LinmotInitProcedure() {
    long now = System.currentTimeMillis();
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_OFF_MODE.getShort(); // Off Mode
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort();
      list.add(new TimedLinmotPutEvent(now + 500, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_ERR_ACK.getShort(); // Error Acknowledgment
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort();
      list.add(new TimedLinmotPutEvent(now + 1000, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_HOME.getShort(); // Home
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_ZEROS.getShort();
      list.add(new TimedLinmotPutEvent(now + 3000, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = LinmotPutConfiguration.CMD_OPERATION.getShort(); // Operation
      linmotPutEvent.motion_cmd_hdr = LinmotPutConfiguration.MC_POSITION.getShort(); // Position
      list.add(new TimedLinmotPutEvent(now + 3500, linmotPutEvent));
    }
  }
}
