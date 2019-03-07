// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class FirstLogMessageTest extends TestCase {
  public void testSimple() throws IOException {
    GokartLogInterface gokartLogInterface = GokartLogAdapterTest.FULL;
    Optional<ByteBuffer> optional = FirstLogMessage.of( //
        gokartLogInterface.file(), //
        GokartPoseChannel.INSTANCE.channel());
    assertTrue(optional.isPresent());
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(optional.get());
    assertEquals(gokartPoseEvent.asVector(), Tensors.vector(36.76454127060236, 42.98864352708994, 1.6236701887263347, 0.7207760810852051));
  }
}
