// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

// TODO document
public interface DvsDavisEventListener extends DavisEventListener {
  void dvs(DvsDavisEvent dvsDavisEvent);

  void imu(ImuDavisEvent imuDavisEvent);
}
