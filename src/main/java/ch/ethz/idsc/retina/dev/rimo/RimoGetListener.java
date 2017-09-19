// code by jph
package ch.ethz.idsc.retina.dev.rimo;

/** receives rimo get events from left and right wheel */
public interface RimoGetListener {
  void rimoGet(RimoGetEvent rimoGetEventL, RimoGetEvent rimoGetEventR);
}
