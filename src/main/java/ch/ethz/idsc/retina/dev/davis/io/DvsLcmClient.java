package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.lcm.LcmClientInterface;

public interface DvsLcmClient extends LcmClientInterface {
  void addDvsListener(DavisDvsListener davisDvsListener);

  void removeDvsListener(DavisDvsListener davisDvsListener);

  void messageReceived(ByteBuffer byteBuffer);
}
