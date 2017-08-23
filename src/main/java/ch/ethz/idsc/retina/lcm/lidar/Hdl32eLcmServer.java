// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.core.StartAndStoppable;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eLiveFiringClient;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eLivePositioningClient;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public class Hdl32eLcmServer implements StartAndStoppable {
  private final Hdl32eLiveFiringClient fir = new Hdl32eLiveFiringClient();
  private final Hdl32eLivePositioningClient pos = new Hdl32eLivePositioningClient();

  public Hdl32eLcmServer(String lidarId) {
    fir.addListener(new BinaryBlobPublisher(Hdl32eLcmChannels.firing(lidarId)));
    pos.addListener(new BinaryBlobPublisher(Hdl32eLcmChannels.positioning(lidarId)));
  }

  @Override
  public void start() {
    fir.start();
    pos.start();
  }

  @Override
  public void stop() {
    fir.stop();
    pos.stop();
  }

  public static void main(String[] args) {
    Hdl32eLcmServer server = new Hdl32eLcmServer("center");
    server.start();
  }
}
