// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

public class Hdl32eRotationEvent {
  public final int usec;
  public final int rotation;

  public Hdl32eRotationEvent(int usec, int rotation) {
    this.usec = usec;
    this.rotation = rotation;
  }
}
