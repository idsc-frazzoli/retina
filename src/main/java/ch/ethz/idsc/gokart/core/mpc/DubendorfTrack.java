package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

public class DubendorfTrack extends BSplineTrack {
  public static final DubendorfTrack HYPERLOOP_EIGHT = hyperloop_eight();
  public static final DubendorfTrack HYPERLOOP_EIGHT_REVERSE = hyperloop_eight_reverse();

  private DubendorfTrack(Tensor controlPointsX, Tensor controlPointsY, Tensor radiusControlPoints) {
    super(controlPointsX, controlPointsY, radiusControlPoints);
  }

  private static Tensor getConstantRadius(int lenght, Scalar radius) {
    Tensor radiusCtrPoints = Tensors.empty();
    for (int i = 0; i < lenght; i++) {
      radiusCtrPoints.add(radius);
    }
    return radiusCtrPoints;
  }

  private static DubendorfTrack hyperloop_eight() {
    Tensor controlPoints = ResourceData.of("/dubilab/controlpoints/eight/20180603.csv");
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    // TODO: do this smarter
    for (int i = 0; i < controlPoints.length(); i++) {
      controlPointsX.add(controlPoints.get(i).Get(0));
      controlPointsY.add(controlPoints.get(i).Get(1));
    }
    return new DubendorfTrack(controlPointsX, controlPointsY, //
        getConstantRadius(controlPoints.length(), Quantity.of(4, SI.METER)));
  }

  private static DubendorfTrack hyperloop_eight_reverse() {
    Tensor controlPoints = Reverse.of(ResourceData.of("/dubilab/controlpoints/eight/20180603.csv"));
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    // TODO: do this smarter
    for (int i = 0; i < controlPoints.length(); i++) {
      controlPointsX.add(controlPoints.get(i).Get(0));
      controlPointsY.add(controlPoints.get(i).Get(1));
    }
    return new DubendorfTrack(controlPointsX, controlPointsY, //
        getConstantRadius(controlPoints.length(), Quantity.of(4, SI.METER)));
  }
}
