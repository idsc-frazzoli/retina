// code by jph
package ch.ethz.idsc.retina.davis;

import ch.ethz.idsc.retina.davis.io.Aedat31FrameEvent;

@FunctionalInterface
public interface Aedat31FrameListener {
  void frameEvent(Aedat31FrameEvent aedat31FrameEvent);
}
