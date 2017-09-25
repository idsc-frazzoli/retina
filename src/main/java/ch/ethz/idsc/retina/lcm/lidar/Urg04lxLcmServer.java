// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxLiveProvider;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** publishes urg04lx binary packets via lcm
 * see also {@link Urg04lxLcmClient} */
public class Urg04lxLcmServer implements StartAndStoppable {
  /** @param lidarId */
  public Urg04lxLcmServer(String lidarId) {
    BinaryBlobPublisher publisher = new BinaryBlobPublisher(Urg04lxDevice.channel(lidarId));
    Urg04lxLiveProvider.INSTANCE.addListener(publisher);
  }

  @Override
  public void start() {
    Urg04lxLiveProvider.INSTANCE.start();
  }

  @Override
  public void stop() {
    Urg04lxLiveProvider.INSTANCE.stop();
  }
}
