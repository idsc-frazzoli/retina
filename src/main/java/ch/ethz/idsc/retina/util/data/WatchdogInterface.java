// code by jph
package ch.ethz.idsc.retina.util.data;

// TODO JPH document
public interface WatchdogInterface {
  void notifyWatchdog();

  /** @return */
  boolean isWatchdogBarking();
}
