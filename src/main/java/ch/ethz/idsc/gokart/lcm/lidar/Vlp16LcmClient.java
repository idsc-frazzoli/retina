// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import ch.ethz.idsc.gokart.lcm.LcmClientInterface;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;

public class Vlp16LcmClient implements LcmClientInterface {
  private final VelodyneLcmClient velodyneLcmClient;

  public Vlp16LcmClient(VelodyneDecoder velodyneDecoder, String lidarId) {
    velodyneLcmClient = new VelodyneLcmClient(VelodyneModel.VLP16, velodyneDecoder, lidarId);
  }

  @Override // from LcmClientInterface
  public void startSubscriptions() {
    velodyneLcmClient.startSubscriptions();
  }

  @Override // from LcmClientInterface
  public void stopSubscriptions() {
    velodyneLcmClient.stopSubscriptions();
  }
}
