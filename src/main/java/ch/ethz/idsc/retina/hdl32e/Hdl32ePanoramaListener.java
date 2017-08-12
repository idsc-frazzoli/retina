// code by jph
package ch.ethz.idsc.retina.hdl32e;

import ch.ethz.idsc.retina.hdl32e.img.Hdl32ePanorama;

public interface Hdl32ePanoramaListener extends AutoCloseable {
  void panorama(Hdl32ePanorama hdl32ePanorama);
}
