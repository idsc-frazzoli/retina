// code by jph
package ch.ethz.idsc.retina.util.gps;

public interface GprmcListener {
  /** @param gprmc */
  void gprmcReceived(Gprmc gprmc);
}
