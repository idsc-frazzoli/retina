// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

public class DubendorfTrack extends MPCBSplineTrack {
  public static final DubendorfTrack HYPERLOOP_EIGHT = hyperloop_eight();
  public static final DubendorfTrack HYPERLOOP_EIGHT_REVERSE = hyperloop_eight_reverse();
  //public static final DubendorfTrack CHICANE = chicane_track();

  private static Tensor getConstantRadius(int length, Scalar radius) {
    // TODO use Tensors.vector(i->radius, length);
    Tensor radiusCtrPoints = Tensors.empty();
    for (int i = 0; i < length; ++i)
      radiusCtrPoints.append(radius);
    return radiusCtrPoints;
  }

  private static DubendorfTrack hyperloop_eight() {
    Tensor controlPoints = ResourceData.of("/dubilab/controlpoints/eight/20180603.csv").multiply(Quantity.of(1, SI.METER));
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    // TODO: do this smarter
    for (int i = 0; i < controlPoints.length(); i++) {
      Tensor iTensor = controlPoints.get(i);
      Scalar x = iTensor.Get(0);
      Scalar y = iTensor.Get(1);
      controlPointsX.append(x);
      controlPointsY.append(y);
    }
    return new DubendorfTrack(controlPointsX, controlPointsY, //
        getConstantRadius(controlPoints.length(), Quantity.of(2, SI.METER)));
  }

  private static DubendorfTrack hyperloop_eight_reverse() {
    Tensor controlPoints = Reverse.of(ResourceData.of("/dubilab/controlpoints/eight/20180603.csv")).multiply(Quantity.of(1, SI.METER));
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    // TODO: do this smarter
    for (int i = 0; i < controlPoints.length(); i++) {
      Tensor iTensor = controlPoints.get(i);
      Scalar x = iTensor.Get(0);
      Scalar y = iTensor.Get(1);
      controlPointsX.append(x);
      controlPointsY.append(y);
    }
    return new DubendorfTrack(controlPointsX, controlPointsY, //
        getConstantRadius(controlPoints.length(), Quantity.of(2, SI.METER)));
  }

  private static DubendorfTrack chicane_track() {
    Tensor controlPoints = Reverse.of(ResourceData.of("/dubilab/controlpoints/chicane/chicane.csv")).multiply(Quantity.of(1, SI.METER));
    Tensor controlPointsX = Tensors.empty();
    Tensor controlPointsY = Tensors.empty();
    // TODO: do this smarter
    for (int i = 0; i < controlPoints.length(); i++) {
      Tensor iTensor = controlPoints.get(i);
      Scalar x = iTensor.Get(0);
      Scalar y = iTensor.Get(1);
      controlPointsX.append(x);
      controlPointsY.append(y);
    }
    return new DubendorfTrack(controlPointsX, controlPointsY, //
        getConstantRadius(controlPoints.length(), Quantity.of(2, SI.METER)));
  }

  // ---
  private DubendorfTrack(Tensor controlPointsX, Tensor controlPointsY, Tensor radiusControlPoints) {
    super(controlPointsX, controlPointsY, radiusControlPoints);
  }
}
