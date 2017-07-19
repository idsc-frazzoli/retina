// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

public interface LaserPositionConsumer {
  /** @param position_data [x0, y0, z0, x1, y1, z1, ...]
   * @param length */
  void digest(float[] position_data, int length);
}
