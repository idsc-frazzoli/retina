// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

@FunctionalInterface
public interface Vmu931ImuFrameListener {
  void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame);
}
