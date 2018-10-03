// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import ch.ethz.idsc.retina.lcm.BinaryLcmClient;

public abstract class SeyeAbstractLcmClient extends BinaryLcmClient {
  private final String channel;

  public SeyeAbstractLcmClient(String channel) {
    this.channel = channel;
  }

  @Override
  protected final String channel() {
    return "seye." + channel + "." + type();
  }

  protected abstract String type();
}
