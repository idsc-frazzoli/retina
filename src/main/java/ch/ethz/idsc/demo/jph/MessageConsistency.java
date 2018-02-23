// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;

public enum MessageConsistency implements OfflineLogListener {
  INSTANCE;
  // ---
  public static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    // System.out.println(time + " " + channel + " " + byteBuffer.remaining());
  }

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dhl : DubendorfHangarLog.values()) {
      System.out.println(dhl);
      OfflineLogPlayer.process(dhl.file(LOG_ROOT), INSTANCE);
    }
  }
}
