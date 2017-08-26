// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

public class LidarRotationEvent {
  public final int usec;
  public final int rotation;

  public LidarRotationEvent(int usec, int rotation) {
    this.usec = usec;
    this.rotation = rotation;
  }
}
