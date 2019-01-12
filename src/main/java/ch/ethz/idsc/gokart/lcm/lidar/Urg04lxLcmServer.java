// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxLiveProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** publishes urg04lx binary packets via lcm
 * see also {@link Urg04lxLcmClient} */
public class Urg04lxLcmServer implements StartAndStoppable {
  private final BinaryBlobPublisher publisher;

  /** @param lidarId */
  public Urg04lxLcmServer(String lidarId) {
    publisher = new BinaryBlobPublisher(Urg04lxDevice.channel(lidarId));
  }

  @Override // from StartAndStoppable
  public void start() {
    Urg04lxLiveProvider.INSTANCE.addListener(publisher);
    Urg04lxLiveProvider.INSTANCE.start();
  }

  @Override // from StartAndStoppable
  public void stop() {
    Urg04lxLiveProvider.INSTANCE.stop();
    Urg04lxLiveProvider.INSTANCE.removeListener(publisher);
  }
}
