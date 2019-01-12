// code by jph
package ch.ethz.idsc.retina.davis.io;

import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;

public abstract class SeyeAbstractLcmClient extends BinaryLcmClient {
  public SeyeAbstractLcmClient(String channel, String type) {
    super("seye." + channel + "." + type);
  }
}
