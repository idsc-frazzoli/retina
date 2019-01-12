// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import java.util.Objects;

import ch.ethz.idsc.gokart.lcm.lidar.Mark8LcmServer;
import ch.ethz.idsc.retina.lidar.mark8.Mark8DeflateDigest;
import ch.ethz.idsc.retina.lidar.mark8.Mark8Digest;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public class Mark8LcmServerModule extends AbstractModule {
  private static final String IP = "192.168.1.3";
  // ---
  private Mark8LcmServer mark8LcmServer = null;

  @Override
  protected void first() throws Exception {
    final int returns = Mark8LcmServer.DEFAULT_RETURNS;
    Mark8Digest mark8Digest = new Mark8DeflateDigest(returns);
    mark8LcmServer = new Mark8LcmServer(IP, mark8Digest, "center");
    mark8LcmServer.start();
  }

  @Override
  protected void last() {
    if (Objects.nonNull(mark8LcmServer)) {
      mark8LcmServer.stop();
      mark8LcmServer = null;
    }
  }
}
