// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

public class Urg04lxRangeEvent {
  public final long timestamp;
  /** unfiltered distance readings the index corresponds to a fixed angle
   * values are in unit meters [m] */
  public final double[] range;

  public Urg04lxRangeEvent(long timestamp, double[] range) {
    this.timestamp = timestamp;
    this.range = range;
  }
}
