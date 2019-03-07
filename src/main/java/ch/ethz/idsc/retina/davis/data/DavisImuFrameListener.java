// code by jph
package ch.ethz.idsc.retina.davis.data;

/** provides the compiled */
@FunctionalInterface
public interface DavisImuFrameListener {
  void imuFrame(DavisImuFrame davisImuFrame);
}
