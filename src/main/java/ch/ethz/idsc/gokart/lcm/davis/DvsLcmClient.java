// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.LcmClientInterface;
import ch.ethz.idsc.retina.davis.DavisDvsListener;

public interface DvsLcmClient extends LcmClientInterface {
  void addDvsListener(DavisDvsListener davisDvsListener);

  void removeDvsListener(DavisDvsListener davisDvsListener);

  void messageReceived(ByteBuffer byteBuffer);
}
