// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

// TODO document
public interface DavisEventListener {
  void aps(ApsDavisEvent apsDavisEvent);

  void dvs(DvsDavisEvent dvsDavisEvent);

  void imu(ImuDavisEvent imuDavisEvent);
}
