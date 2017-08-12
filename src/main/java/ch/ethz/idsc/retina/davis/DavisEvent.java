// code by jph
package ch.ethz.idsc.retina.davis;

import java.io.Serializable;

/** all davis event types extend from this interface */
public interface DavisEvent extends Serializable {
  /** @return timestamp of event in [us] */
  int time();
}
