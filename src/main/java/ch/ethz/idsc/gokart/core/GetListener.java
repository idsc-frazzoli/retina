// code by jph
package ch.ethz.idsc.gokart.core;

/** receives messages from one of the four UDP connections with the micro-autobox.
 * The four channels are: rimo, steer, linmot, misc */
@FunctionalInterface
public interface GetListener<GE> {
  /** @param getEvent received from the micro-autobox */
  void getEvent(GE getEvent);
}
