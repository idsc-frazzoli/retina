// code by jph
package ch.ethz.idsc.gokart.core.plan;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.sca.Chop;
import idsc.BinaryBlob;
import junit.framework.TestCase;

public class TrajectoryEventsTest extends TestCase {
  public void testDecode() {
    BinaryBlob binaryBlob = ArrayFloatBlob.encode(HilbertMatrix.of(14, 5));
    assertEquals(binaryBlob.data_length, 289);
    assertEquals(binaryBlob.data.length, 289);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    assertEquals(byteBuffer.remaining(), 289);
    assertEquals(byteBuffer.position(), 0);
    Tensor tensor = ArrayFloatBlob.decode(byteBuffer);
    Chop._05.requireClose(tensor, HilbertMatrix.of(14, 5));
  }

  public void testTrajectory() {
    BinaryBlob binaryBlob = ArrayFloatBlob.encode(HilbertMatrix.of(14, 5));
    assertEquals(binaryBlob.data_length, 289);
    assertEquals(binaryBlob.data.length, 289);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    assertEquals(byteBuffer.remaining(), 289);
    assertEquals(byteBuffer.position(), 0);
    List<TrajectorySample> trajectory = TrajectoryEvents.trajectory(byteBuffer);
    assertEquals(trajectory.size(), 14);
    TrajectorySample trajectorySample = trajectory.get(0);
    StateTime stateTime = trajectorySample.stateTime();
    Chop._05.requireClose(stateTime.state(), Tensors.vector(1, 0.5, 1 / 3.0, 0.25));
    Chop._05.requireClose(stateTime.time(), RealScalar.of(0.2));
  }
}
