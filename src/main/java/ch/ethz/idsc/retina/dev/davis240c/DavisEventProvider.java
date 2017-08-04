// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

public interface DavisEventProvider {
  void addListener(DavisEventListener davisEventListener);

  /** function called once in order to start data acquisition via this urg provider
   * listeners are supplied when data is available */
  void start();

  /** stops urg provider */
  void stop();
}
