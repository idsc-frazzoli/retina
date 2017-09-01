// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

public class Urg04lxSpacial {
  private static final int INDEX_LAST = 681;
  // ---
  private final Tensor angle = Subdivide.of(-120 * Math.PI / 180, 120 * Math.PI / 180, INDEX_LAST).unmodifiable();
  private final Tensor direction = Transpose.of(Tensors.of(Cos.of(angle), Sin.of(angle)));

  public Urg04lxSpacial() {
  }
}
