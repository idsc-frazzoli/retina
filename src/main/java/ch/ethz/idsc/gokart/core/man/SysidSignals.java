// code by az
package ch.ethz.idsc.gokart.core.man;

import java.util.function.Supplier;

import ch.ethz.idsc.retina.util.math.PRBS7SignedSignal;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum SysidSignals implements Supplier<ScalarUnaryOperator> {
  PRBS7_SIGNED_FAST() {
    @Override
    public ScalarUnaryOperator get() {
      return PRBS7SignedSignal.of(DoubleScalar.of(0.2));
    }
  }, //
  PRBS7_SIGNED_SLOW() {
    @Override
    public ScalarUnaryOperator get() {
      return PRBS7SignedSignal.of(DoubleScalar.of(0.5));
    }
  }, //
  CHIRP_FAST() {
    @Override
    public ScalarUnaryOperator get() {
      return new ChirpSignal(0.02, 0.256, 10);
    }
  }, //
  CHIRP_SLOW() {
    @Override
    public ScalarUnaryOperator get() {
      return new ChirpSignal(0.02, 0.1, 20);
    }
  }, //
  STEPS() {
    @Override
    public ScalarUnaryOperator get() {
      return new VectorSignal(StaticHelper.incrSteps(15), RealScalar.of(1.6));
    }
  },
  TOGGLE_010() {
    @Override
    public ScalarUnaryOperator get() {
      return ToggleSignal.create(4, 0.1);
    }
  }, //
  TOGGLE_020() {
    @Override
    public ScalarUnaryOperator get() {
      return ToggleSignal.create(4, 0.2);
    }
  }, //
  TOGGLE_030() {
    @Override
    public ScalarUnaryOperator get() {
      return ToggleSignal.create(4, 0.3);
    }
  }, //
  ;
}
