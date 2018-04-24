// code by: az
package ch.ethz.idsc.gokart.core.joy;

import java.util.function.Supplier;

import ch.ethz.idsc.retina.util.math.ChirpSignal;
import ch.ethz.idsc.retina.util.math.PRBS7SignedSignal;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum SysIdRimo implements Supplier<ScalarUnaryOperator> {
  PRBS7_SIGNEDFAST() {
    @Override
    public ScalarUnaryOperator get() {
      return new PRBS7SignedSignal(DoubleScalar.of(0.2));
    }
  },
  PRBS7_SIGNEDSLOW() {
    @Override
    public ScalarUnaryOperator get() {
      return new PRBS7SignedSignal(DoubleScalar.of(0.5));
    }
  },
  CHIRP_FAST() {
    @Override
    public ScalarUnaryOperator get() {
      return new ChirpSignal(0.02, 0.256, 10);
    }
  },
  CHIRP_SLOW() {
    @Override
    public ScalarUnaryOperator get() {
      return new ChirpSignal(0.02, 0.1, 20);
    }
  },;
}
