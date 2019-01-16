// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Round;

public class MPCBSplineTrack extends BSplineTrack implements MPCPreviewableTrack {
  public MPCBSplineTrack(Tensor trackData, Scalar radiusOffset, boolean closed) {
    super(Tensors.of( //
        trackData.get(0), //
        trackData.get(1), //
        trackData.get(2).map(radius -> radius.add(radiusOffset))), closed);
  }

  public MPCBSplineTrack(Tensor trackData, boolean closed) {
    super(trackData, closed);
  }

  @Override
  public MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding) {
    // test if this function is fast enough to be called many times (it should be)
    // long startTime = System.nanoTime();
    // Scalar pathProgress = getNearestPathProgress(position);
    // long endTime = System.nanoTime();
    // long startTimef = System.nanoTime();
    Scalar pathProgress = getNearestPathProgress(position);
    // long endTimef = System.nanoTime();
    // System.out.println("fast:"+ fastProgress+"/slow: "+pathProgress);
    // System.out.println(" path progress timing: "+(endTime-startTime)/1000+"[micros]");
    // System.out.println(" fast path progress timing: "+(endTimef-startTimef)/1000+"[micros]");
    // round down
    // int currentIndex = Floor.of(pathProgress.subtract(RealScalar.of(0.5))).number().intValue();
    int currentIndex = Round.of(pathProgress).number().intValue() - 1;
    // progress=1 at middle point between first 2 control points
    Scalar progressStart = pathProgress.subtract(RealScalar.of(currentIndex)).subtract(RealScalar.of(0.5));
    Tensor ctrX = Tensors.empty();
    Tensor ctrY = Tensors.empty();
    Tensor ctrR = Tensors.empty();
    if (currentIndex < 0)
      currentIndex += numPoints;
    for (int i = 0; i < previewSize; ++i) {
      Tensor vector = controlPoints.get(currentIndex);
      ctrX.append(vector.Get(0));
      ctrY.append(vector.Get(1));
      ctrR.append(Ramp.FUNCTION.apply(controlPointsR.Get(currentIndex).subtract(padding)));
      ++currentIndex;
      if (currentIndex >= numPoints)
        currentIndex = 0;
    }
    return new MPCPathParameter(progressStart, ctrX, ctrY, ctrR);
  }
}
