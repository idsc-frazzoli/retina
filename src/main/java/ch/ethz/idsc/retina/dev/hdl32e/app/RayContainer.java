// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.app;

class RayContainer {
  public float[] position;
  public byte[] intensity;

  public int size() {
    return intensity.length;
  }
}
