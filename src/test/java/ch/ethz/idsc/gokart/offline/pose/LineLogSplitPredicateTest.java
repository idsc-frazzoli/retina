// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.LogSplitPredicate;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LineLogSplitPredicateTest extends TestCase {
  public void testSimple() throws IOException {
    LogSplitPredicate logSplitPredicate = //
        new LineLogSplitPredicate(Tensors.fromString("{10[m],20[m],1.1}"), Quantity.of(2, "m"));
    File file = new File("src/test/resources/localization/vlp16.center.ray_autobox.rimo.get", "log.lcm");
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        assertFalse(logSplitPredicate.split(time, channel, byteBuffer));
      }
    };
    OfflineLogPlayer.process(file, offlineLogListener);
  }
}
