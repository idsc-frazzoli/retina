// code by jph, gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConstants;
import ch.ethz.idsc.gokart.core.DubendorfCurve;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class CurveClothoidPursuitPlannerTest extends TestCase {
  public void testSpecific1() throws Exception {
    // Tensors.fromString("{35.1[m], 44.9[m], 1}");
    ClothoidPursuitConfig clothoidPursuitConfig = ClothoidPursuitConfig.GLOBAL;
    CurveClothoidPursuitPlanner curveClothoidPursuitPlanner = new CurveClothoidPursuitPlanner(clothoidPursuitConfig);
    Tensor curve = DubendorfCurve.TRACK_OVAL_SE2;
    // System.out.println("curve.length==" + curve.length());
    int success = 0;
    for (int index = 0; index < curve.length(); ++index) {
      // System.out.println(index);
      Tensor pose = curve.get(index);
      Tensor speed = Tensors.of(Quantity.of(1, SI.VELOCITY), Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.PER_SECOND));
      Optional<Scalar> optional = curveClothoidPursuitPlanner.getPlan( //
          pose, speed, curve, true).map(ClothoidPlan::ratio);
      if (optional.isPresent()) {
        Scalar ratio = optional.get();
        Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(ratio);
        Clips.absoluteOne().requireInside(angle);
        success++;
      }
    }
    assertTrue(curve.length() / 2 < success);
  }

  public void testSpecific2() throws Exception {
    // Tensors.fromString("{35.1[m], 44.9[m], 1}");
    ClothoidPursuitConfig clothoidPursuitConfig = ClothoidPursuitConfig.GLOBAL;
    CurveClothoidPursuitPlanner curveClothoidPursuitPlanner = new CurveClothoidPursuitPlanner(clothoidPursuitConfig);
    Tensor curve = DubendorfCurve.TRACK_OVAL_SE2;
    // System.out.println("curve.length==" + curve.length());
    Distribution distribution = NormalDistribution.of(0, 0.1);
    int success = 0;
    for (int index = 0; index < curve.length(); ++index) {
      // System.out.println(index);
      Tensor pose = new Se2GroupElement(curve.get(index)).combine(PoseHelper.attachUnits(RandomVariate.of(distribution, 3)));
      Tensor speed = Tensors.of(Quantity.of(1, SI.VELOCITY), Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.PER_SECOND));
      Optional<Scalar> optional = curveClothoidPursuitPlanner.getPlan( //
          pose, speed, curve, true).map(ClothoidPlan::ratio);
      if (optional.isPresent()) {
        Scalar ratio = optional.get();
        Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(ratio);
        Clips.absoluteOne().requireInside(angle);
        ++success;
      }
    }
    assertTrue(curve.length() / 2 < success);
  }

  public void testTransform() {
    Se2GroupElement se2GroupElement = new Se2GroupElement(Tensors.fromString("{2[m], 3[m], 1}"));
    TensorUnaryOperator tensorUnaryOperator = se2GroupElement.inverse()::combine;
    Tensor curve = Tensors.fromString("{{2[m], 3[m], 1}, {3[m], 4[m], 2}}");
    Tensor local = Tensor.of(curve.stream().map(tensorUnaryOperator));
    assertTrue(Chop.NONE.allZero(local.get(0)));
  }

  public void testComparator() {
    List<Tensor> list = new ArrayList<>();
    list.add(Tensors.vector(0, 1, 1));
    list.add(Tensors.vector(1, 1, 4));
    list.add(Tensors.vector(0, 1.1, 0));
    list.add(Tensors.vector(1e-9, 0.5, 2));
    list.add(Tensors.vector(1e-9, 0, 3));
    list.sort(CurveClothoidPursuitPlanner.COMPARATOR);
    assertEquals(Tensors.vector(0, 1, 2, 3, 4), Tensor.of(list.stream().map(t -> t.Get(2))));
  }
}
