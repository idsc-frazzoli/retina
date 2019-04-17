// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class FirstLogMessageTest extends TestCase {
  public void testSimple() throws IOException {
    GokartLogInterface gokartLogInterface = GokartLogAdapterTest.FULL;
    Optional<ByteBuffer> optional = FirstLogMessage.of( //
        gokartLogInterface.file(), //
        GokartPoseChannel.INSTANCE.channel());
    assertTrue(optional.isPresent());
    GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(optional.get());
    Chop._12.requireClose(gokartPoseEvent.getPose(), Tensors.fromString("{36.76454127060236[m], 42.98864352708994[m], 1.6236701887263347}"));
    Chop._12.requireClose(gokartPoseEvent.getQuality(), RealScalar.of(0.7207760810852051));
    assertEquals(gokartPoseEvent.asVector().length(), 4);
    Tensor velocity = gokartPoseEvent.getVelocity();
    assertEquals(velocity.length(), 3);
    assertTrue(Chop.NONE.allZero(velocity));
    assertTrue(Chop.NONE.allZero(gokartPoseEvent.getGyroZ()));
  }
}
