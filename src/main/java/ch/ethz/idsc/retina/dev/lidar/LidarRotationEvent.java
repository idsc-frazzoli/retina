// code by jph
package ch.ethz.idsc.retina.dev.lidar;

public class LidarRotationEvent {
  // TODO document
  public final int usec;
  public final int rotation;

  public LidarRotationEvent(int usec, int rotation) {
    this.usec = usec;
    this.rotation = rotation;
  }
}