// code by jph
package ch.ethz.idsc.retina.lidar;

public class LidarRotationEvent {
  public final int usec;
  public final int rotation;

  /** @param usec micro-second timestamp with periodic overflow every 2^32 us
   * @param rotation */
  public LidarRotationEvent(int usec, int rotation) {
    this.usec = usec;
    this.rotation = rotation;
  }
}
