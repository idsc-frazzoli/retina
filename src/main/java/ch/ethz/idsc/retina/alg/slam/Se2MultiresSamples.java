// code by jph
package ch.ethz.idsc.retina.alg.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/**
 * 
 */
public class Se2MultiresSamples {
  private static final Scalar DECR = RealScalar.of(0.6);
  // ---
  private final List<Tensor> list = new ArrayList<>();

  public Se2MultiresSamples(Scalar shf, Scalar ang, int levels) {
    for (int iterate = 0; iterate < levels; ++iterate) {
      Tensor next = Tensors.empty();
      for (int x = -1; x <= 1; ++x)
        for (int y = -1; y <= 1; ++y)
          for (int t = -1; t <= 1; ++t)
            next.append(Se2Exp.of(Tensors.of( //
                shf.multiply(RealScalar.of(x)), //
                shf.multiply(RealScalar.of(y)), //
                ang.multiply(RealScalar.of(t)))));
      list.add(next);
      // ---
      ang = ang.multiply(DECR);
      shf = shf.multiply(DECR);
    }
  }

  public Tensor level(int level) {
    return list.get(level);
  }

  public int levels() {
    return list.size();
  }
}
