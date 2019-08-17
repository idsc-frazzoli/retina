// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.retina.joystick.ManualControlAdapter;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class MPCDynamicDrivingModuleTest extends TestCase {
  public void testSimple() {
    MPCOptimizationParameterDynamic op1 = MPCDynamicDrivingModule.optimizationParameter(MPCOptimizationConfig.GLOBAL, Optional.empty());
    ManualControlInterface manualControlInterface = new ManualControlAdapter( //
        RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, Tensors.vector(0.3, 0.4), false, false);
    MPCOptimizationParameterDynamic op2 = MPCDynamicDrivingModule.optimizationParameter(MPCOptimizationConfig.GLOBAL, Optional.of(manualControlInterface));
    assertTrue(Scalars.lessThan(op1.speedLimit(), op2.speedLimit()));
    Sign.requirePositive(op1.xAccLimit());
    Sign.requirePositive(op2.xAccLimit());
  }
}
