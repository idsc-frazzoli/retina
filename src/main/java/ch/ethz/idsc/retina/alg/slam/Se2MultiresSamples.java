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

  public static Se2MultiresSamples createDefault() {
    // TODO justify magic const
    Scalar ang = RealScalar.of(2 * Math.PI / 180); // 2 [deg]
    float METER_TO_PIXEL = 50;
    Scalar shf = RealScalar.of(0.03 * METER_TO_PIXEL); // 3 [cm]
    return new Se2MultiresSamples(ang, shf, 4);
  }

  private final List<Tensor> list = new ArrayList<>();

  public Se2MultiresSamples(Scalar ang, Scalar shf, int level) {
    for (int iterate = 0; iterate < level; ++iterate) {
      Tensor next = Tensors.empty();
      for (int x = -1; x <= 1; ++x)
        for (int y = -1; y <= 1; ++y)
          for (int t = -1; t <= 1; ++t) {
            Tensor test = Se2Sampler.get( //
                ang.multiply(RealScalar.of(t)), //
                shf.multiply(RealScalar.of(x)), //
                shf.multiply(RealScalar.of(y)));
            next.append(test);
          }
      list.add(next);
      // ---
      ang = ang.multiply(DECR);
      shf = shf.multiply(DECR);
    }
  }

  public Tensor level(int level) {
    return list.get(level);
  }
}
