// code by jph
package ch.ethz.idsc.gokart.core;

public interface PutListener<PE> {
  /** @param putEvent sent to micro-autobox */
  void putEvent(PE putEvent);
}
