// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

@FunctionalInterface
public interface Vmu931FrameListener {
  void vmu931Frame(Vmu931Frame vmu931Frame);
}
