// code by jph
package ch.ethz.idsc.gokart.core;

import java.io.File;

import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.crv.subdiv.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;

/** .
 * ante 20190430: curve points were unitless
 * post 20190430: points of any curve have units */
public enum OvalTrack {
  ;
  private static final TensorUnaryOperator SUBDIVISION_SE2 = //
      new FourPointCurveSubdivision(Se2Geodesic.INSTANCE)::cyclic;
  // ---
  public static final Tensor R2 = track_oval();
  public static final Tensor SE2 = track_oval_se2();

  /** matrix with 2 columns */
  private static Tensor project_se2_r2(Tensor control) {
    return Tensor.of(control.stream().map(Extract2D.FUNCTION));
  }

  private static Tensor track_oval() {
    Tensor poly = track_oval_se2();
    return Tensors.isEmpty(poly) //
        ? Tensors.empty()
        : project_se2_r2(poly).unmodifiable();
  }

  private static Tensor track_oval_se2() {
    try {
      Tensor poly = Import.of(new File("resources/track20190325.csv"));
      poly = Tensor.of(poly.stream().map(PoseHelper::attachUnits));
      return Nest.of(SUBDIVISION_SE2, poly, 4).unmodifiable();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return Tensors.empty();
  }
}
