// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

@FunctionalInterface
public interface Hdl32ePosListener {
  void positioning(Hdl32ePosEvent hdl32ePosEvent);
}
