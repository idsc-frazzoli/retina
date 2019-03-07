// code by jph
package ch.ethz.idsc.gokart.core;

/** receives the command that was issued to one of the four UDP connections
 * with the micro-autobox. The four channels are: rimo, steer, linmot, misc */
@FunctionalInterface
public interface PutListener<PE> {
  /** @param putEvent sent to micro-autobox */
  void putEvent(PE putEvent);
}
