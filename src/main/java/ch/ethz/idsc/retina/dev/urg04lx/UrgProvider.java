// code by jph
package ch.ethz.idsc.retina.dev.urg04lx;

public interface UrgProvider {
  static final String URG_PREFIX = "URG{";

  /** @param urgListener */
  void addListener(UrgListener urgListener);

  /** function called once in order to start data acquisition via this urg provider
   * listeners are supplied when data is available */
  void start();

  /** stops urg provider */
  void stop();
}
