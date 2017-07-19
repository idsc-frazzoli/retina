// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

public interface HDL32EFiringPacketConsumer {
  void lasers(byte[] laser_data);
}
