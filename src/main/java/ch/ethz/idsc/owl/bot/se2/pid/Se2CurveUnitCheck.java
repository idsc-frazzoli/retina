package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

public enum Se2CurveUnitCheck {
  ;
  /** @param curve
   * @return true if each curve element has {x,y,phi} else false */
  public static boolean that(Tensor curve, Unit units) {
    boolean condition = true;
    for (int index = 0; index < curve.length(); ++index) {
      condition &= poseHasUnits(curve.get(index), units);
    }
    if (!condition && UserName.is("maximilien"))
      System.err.println("Curve with missing units");
    return condition;
  }

  /** @param pose
   * @param unit
   * @return true if pose has wanted units */
  public static boolean poseHasUnits(Tensor pose, Unit unit) {
    boolean condition = true;
    for (int index = 0; index < pose.length() - 1; ++index) {
      condition &= scalarHasUnits(pose.Get(index), unit);
    }
    condition &= scalarHasUnits(pose.Get(2), SI.ONE);
    return condition;
  }

  /** @param scalar
   * @param unit
   * @return true if scalar has wanted units */
  public static boolean scalarHasUnits(Scalar scalar, Unit unit) {
    boolean condition = Quantity.of(scalar.number(), unit).equals(scalar);
    return condition;
  }
}
