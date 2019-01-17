// code by jph
package ch.ethz.idsc.retina.util.data;

/** watchdog as on a microcontroller
 * 
 * a watchdog has a timeout period, for instance 0.1[s]
 * unless the watchdog is notified during this period
 * the watchdog will be "barking" after this period.
 * The timing resets as soon as the watchdog is notified.
 * 
 * There are 2 types of watchdog:
 * 1) recoverable, meaning, that even when the watchdog
 * is already "barking", it's notification will reset the
 * timing and the watchdog goes back to the non-barking state.
 * 2) un-recoverable, means, that a watchdog never leaves
 * the "barking" state even after notification. */
public interface Watchdog {
  /** notify watchdog */
  void notifyWatchdog();

  /** @return whether this watchdog is in "barking" state */
  boolean isBarking();
}
