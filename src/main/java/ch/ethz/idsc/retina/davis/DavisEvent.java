// code by jph
package ch.ethz.idsc.retina.davis;

/** all davis event types extend from this interface */
public interface DavisEvent {
  /** @return timestamp of event in [us] */
  int time();
}
