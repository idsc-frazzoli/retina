// code by edo
package ch.ethz.idsc.owly.car.drift;

import java.util.Collections;

import ch.ethz.idsc.owly.glc.core.GoalInterface;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DriftGoalManagerTest extends TestCase {
  public void testSimple() {
    GoalInterface goalInterface = DriftGoalManager.createStandard(//
        Tensors.vector(0, 0, 0, -0.3055, 0.5032, 8), //
        Tensors.vector( //
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, //
            0.05, 0.05, 0.25));
    assertFalse(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(1, 2, 3, -0.3, 0.5, 7.9), RealScalar.ZERO))));
    assertFalse(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(10, 2, 3, -0.3, 0.5, 7.9), RealScalar.ZERO))));
    assertFalse(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(100, 200, 3, -0.3, 0.5, 7.9), RealScalar.ZERO))));
    assertFalse(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(1, 2000, 3000, -0.3, 0.5, 7.9), RealScalar.ZERO))));
  }

  public void testDisjoint() {
    GoalInterface goalInterface = DriftGoalManager.createStandard(//
        Tensors.vector(0, 0, 0, -0.3055, 0.5032, 8), //
        Tensors.vector( //
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, //
            0.05, 0.05, 0.25));
    assertTrue(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(1, 2, 3, -0.2, 0.5, 7.9), RealScalar.ZERO))));
    assertTrue(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(10, 2, 3, -0.3, 0.4, 7.9), RealScalar.ZERO))));
    assertTrue(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(100, 200, 3, -0.3, 0.5, 8.9), RealScalar.ZERO))));
    assertTrue(goalInterface.isDisjoint(Collections.singletonList( //
        new StateTime(Tensors.vector(1, 2000, 3000, -0.3, 0.5, 9.9), RealScalar.ZERO))));
  }
}
