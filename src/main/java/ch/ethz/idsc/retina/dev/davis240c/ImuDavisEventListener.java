// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

// TODO document
public interface ImuDavisEventListener extends DavisEventListener {
  void imu(ImuDavisEvent imuDavisEvent);
}
