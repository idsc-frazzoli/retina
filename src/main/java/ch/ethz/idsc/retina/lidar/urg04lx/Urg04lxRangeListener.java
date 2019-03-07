// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

@FunctionalInterface
public interface Urg04lxRangeListener {
  void urg04lxRange(Urg04lxRangeEvent urg04lxRangeEvent);
}
