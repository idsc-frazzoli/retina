// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.app;

class FiringContainer {
  public float[] position;
  public byte[] intensity;

  public int size() {
    return intensity.length;
  }
}
