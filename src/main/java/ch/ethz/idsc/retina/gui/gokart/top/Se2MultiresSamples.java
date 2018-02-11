// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** j2b2 project */
public class Se2MultiresSamples {
  private static final Scalar DECR = DoubleScalar.of(0.6);
  // ---
  private final List<Tensor> list = new ArrayList<>();

  /** @param shift may be in physical space or pixel space
   * @param angle in radians
   * @param levels */
  public Se2MultiresSamples(Scalar shift, Scalar angle, int levels, final int fan) {
    for (int iterate = 0; iterate < levels; ++iterate) {
      Tensor next = Tensors.empty();
      for (int x = -fan; x <= fan; ++x)
        for (int y = -fan; y <= fan; ++y)
          for (int t = -fan; t <= fan; ++t)
            next.append(Se2Exp.of(Tensors.of( //
                shift.multiply(DoubleScalar.of(x)), //
                shift.multiply(DoubleScalar.of(y)), //
                angle.multiply(DoubleScalar.of(t)))));
      list.add(next);
      // ---
      angle = angle.multiply(DECR);
      shift = shift.multiply(DECR);
    }
  }

  public Tensor level(int level) {
    return list.get(level);
  }

  public int levels() {
    return list.size();
  }
}
