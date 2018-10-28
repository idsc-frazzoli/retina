// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class MPCBSplineTrack extends BSplineTrack implements MPCPreviewableTrack {
  public MPCBSplineTrack(Tensor controlPointsX, Tensor controlPointsY, Tensor radiusControlPoints) {
    super(controlPointsX, controlPointsY, radiusControlPoints);
  }

  @Override
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position) {
    // test if this function is fast enough to be called many times (it should be)
    Scalar pathProgress = getNearestPathProgress(position);
    // use java cast because it always rounds down (not that clear in Tensor)
    int currentIndex = (int) pathProgress.number().floatValue();
    Tensor ctrX = Tensors.empty();
    Tensor ctrY = Tensors.empty();
    Tensor ctrR = Tensors.empty();
    for (int i = 0; i < previewSize; i++) {
      // TODO find out: is this efficient?
      ctrX.append(controlPointsX.Get(currentIndex));
      ctrY.append(controlPointsY.Get(currentIndex));
      ctrR.append(controlPointsR.Get(currentIndex));
      currentIndex++;
      if (currentIndex >= numPoints)
        currentIndex = 0;
    }
    return new MPCPathParameter(ctrX, ctrY, ctrR);
  }
}
