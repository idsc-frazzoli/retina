package ch.ethz.idsc.retina.dev.linmot;

public class TimedLinmotPutEvent implements Comparable<TimedLinmotPutEvent> {
  public final long time_ms;
  public final LinmotPutEvent linmotPutEvent;

  public TimedLinmotPutEvent(long time_ms, LinmotPutEvent linmotPutEvent) {
    this.time_ms = time_ms;
    this.linmotPutEvent = linmotPutEvent;
  }

  @Override
  public int compareTo(TimedLinmotPutEvent arg0) {
    return Long.compare(time_ms, arg0.time_ms);
  }
}
