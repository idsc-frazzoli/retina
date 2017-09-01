// code by jph
package ch.ethz.idsc.retina.dev.lidar.hdl32e.data;

public interface Hdl32ePanoramaListener extends AutoCloseable {
  void panorama(Hdl32ePanorama hdl32ePanorama);
}
