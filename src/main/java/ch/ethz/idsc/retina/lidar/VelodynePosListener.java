// code by jph
package ch.ethz.idsc.retina.lidar;

@FunctionalInterface
public interface VelodynePosListener {
  void velodynePos(VelodynePosEvent velodynePosEvent);
}
