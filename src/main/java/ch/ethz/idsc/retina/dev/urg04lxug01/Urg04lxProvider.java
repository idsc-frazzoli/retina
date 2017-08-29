// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

public interface Urg04lxProvider {
  static final String URG_PREFIX = "UR";

  /** @param urgListener */
  void addListener(Urg04lxEventListener urgListener);

  /** function called once in order to start data acquisition via this urg provider
   * listeners are supplied when data is available */
  void start();

  /** stops urg provider */
  void stop();
}
