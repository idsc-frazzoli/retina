// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

public class Urg04lxEvent {
  public final long timestamp;
  public final short[] range;

  public Urg04lxEvent(long timestamp, short[] range) {
    this.timestamp = timestamp;
    this.range = range;
  }
}
