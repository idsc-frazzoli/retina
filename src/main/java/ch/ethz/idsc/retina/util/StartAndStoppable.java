// code by jph
package ch.ethz.idsc.retina.util;

/** universal interface to allow for starting and stopping a process/computation */
public interface StartAndStoppable {
  /** start action */
  void start();

  /** stop action */
  void stop();
}
