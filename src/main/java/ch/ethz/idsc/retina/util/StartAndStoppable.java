// code by jph
package ch.ethz.idsc.retina.util;

/** universal interface to allow for starting and stopping a process/computation */
public interface StartAndStoppable {
  void start();

  void stop();
}
