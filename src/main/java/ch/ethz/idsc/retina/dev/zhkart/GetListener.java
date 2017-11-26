// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

public interface GetListener<GE> {
  /** @param getEvent received from the micro-autobox */
  void getEvent(GE getEvent);
}
