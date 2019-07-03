package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import idsc.BinaryBlob;
import junit.framework.TestCase;

public class PursuitPlanLcmTest extends TestCase {
  private static final Tensor POSE = Tensors.fromString("{1[m], 10[m], 1}");
  private static final Tensor LOOK_AHEAD = Tensors.fromString("{2[m], 20[m], 2}");
  private static final boolean IS_FORWARD = false;

  private static ByteBuffer setup() {
    BinaryBlob blob = PursuitPlanLcm.encode(IS_FORWARD, POSE, LOOK_AHEAD);
    ByteBuffer byteBuffer = ByteBuffer.wrap(blob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    return byteBuffer;
  }

  public void testPose() {
    ByteBuffer byteBuffer = setup();
    Tensor pose = PursuitPlanLcm.decodePose(byteBuffer);
    assertEquals(POSE, pose);
  }

  public void testLookAhead() {
    ByteBuffer byteBuffer = setup();
    Tensor lookAhead = PursuitPlanLcm.decodeLookAhead(byteBuffer);
    assertEquals(LOOK_AHEAD, lookAhead);
  }

  public void testIsForward() {
    ByteBuffer byteBuffer = setup();
    Optional<Boolean> isForward = PursuitPlanLcm.decodeIsForward(byteBuffer);
    assertTrue(isForward.isPresent());
    assertEquals(IS_FORWARD, (boolean) isForward.get());
  }
}
