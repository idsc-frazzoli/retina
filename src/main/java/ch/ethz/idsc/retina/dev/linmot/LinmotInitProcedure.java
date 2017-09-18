package ch.ethz.idsc.retina.dev.linmot;

import java.util.PriorityQueue;
import java.util.Queue;

public class LinmotInitProcedure {
  public final Queue<TimedLinmotPutEvent> list = new PriorityQueue<>();

  public LinmotInitProcedure() {
    long now = System.currentTimeMillis();
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = 0x003e; // Off Mode
      linmotPutEvent.motion_cmd_hdr = 0x0000;
      list.add(new TimedLinmotPutEvent(now + 500, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = 0x00bf; // Error Acknowledgment
      linmotPutEvent.motion_cmd_hdr = 0x0000;
      list.add(new TimedLinmotPutEvent(now + 1000, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = 0x083f; // Home
      linmotPutEvent.motion_cmd_hdr = 0x0000;
      list.add(new TimedLinmotPutEvent(now + 3000, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent();
      linmotPutEvent.control_word = 0x003f; // Operation
      linmotPutEvent.motion_cmd_hdr = 0x0900; // Position
      list.add(new TimedLinmotPutEvent(now + 3500, linmotPutEvent));
    }
  }
}
