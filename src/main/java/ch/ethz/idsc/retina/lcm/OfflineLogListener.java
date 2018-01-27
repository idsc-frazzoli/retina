// code by jph
package ch.ethz.idsc.retina.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;
import lcm.logging.Log.Event;

public interface OfflineLogListener {
  void event(Scalar time, Event event, ByteBuffer byteBuffer);
}
