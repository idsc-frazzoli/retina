// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxDevice;
import ch.ethz.idsc.retina.dev.lidar.urg04lx.Urg04lxLiveProvider;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** publishes urg04lx binary packets via lcm */
public enum Urg04lxLcmServer implements StartAndStoppable {
  INSTANCE;
  // ---
  private Urg04lxLcmServer() {
    String lidarId = "front";
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

  public static void main(String[] args) throws Exception {
    INSTANCE.start();
    // System.out.println("started");
    // Thread.sleep(5000);
    // INSTANCE.stop();
    // System.out.println("stopped");
  }
}
