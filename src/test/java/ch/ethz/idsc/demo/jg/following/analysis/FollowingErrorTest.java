// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Abs;
import junit.framework.TestCase;

public class FollowingErrorTest extends TestCase {
  private final static Tensor BASE_TENSOR = Tensors.vector(1, 0, 0).unmodifiable();
  private final static Tensor ERROR = Tensors.vector(0, 1, 1).unmodifiable();
  private final static Tensor REFERENCE = Tensor.of(IntStream.range(0, 100).mapToObj(RealScalar::of).map(BASE_TENSOR::multiply)).unmodifiable();

  public void testSimple() {
    FollowingError followingError = setup();
    Tensor trajectory = Tensor.of(REFERENCE.stream().map(ERROR::add)).unmodifiable();
    AtomicInteger timer = new AtomicInteger(0);
    trajectory.forEach(pose -> followingError.insert(Quantity.of(timer.getAndIncrement() / 100, SI.SECOND), pose));
    // ---
    assertTrue(followingError.averageError().isPresent());
    assertEquals(followingError.averageError().get().Get(0), Norm._2.ofVector(ERROR.extract(0, 2)));
    assertEquals(followingError.averageError().get().Get(1), ERROR.Get(2).abs());
    // ---
    assertTrue(followingError.accumulatedError().isPresent());
    assertEquals(followingError.accumulatedError().get().Get(0), //
        Norm._2.ofVector(ERROR.extract(0, 2)).multiply(RealScalar.of(trajectory.length())));
    assertEquals(followingError.accumulatedError().get().Get(1), //
        Abs.of(ERROR.Get(2)).multiply(RealScalar.of(trajectory.length())));
  }

  public void testEmpty() {
    FollowingError followingError = setup();
    assertFalse(followingError.averageError().isPresent());
  }

  private static FollowingError setup() {
    FollowingError followingError = new FollowingError();
    followingError.setReference(REFERENCE);
    return followingError;
  }
}
