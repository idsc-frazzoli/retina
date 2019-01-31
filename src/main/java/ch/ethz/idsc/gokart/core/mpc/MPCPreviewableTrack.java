// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface MPCPreviewableTrack {
  /** get the path parameter for MPC
   * @param previewSize number of control points that are to be given (fixed in C code)
   * @param position the current position in track frame [x[m], y[m]]
   * @param padding the padding added to the side of the track [m]
   * @return resulting MPC path parameter to be sent to MPC */
  MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding);

  /** get the path parameter for MPC
   * @param previewSize number of control points that are to be given (fixed in C code)
   * @param position the current position in track frame [x[m], y[m]]
   * @param padding the padding added to the side of the track [m]
   * @param qpFactor the quadratic process factor [1] (redefinition possible)
   * @return resulting MPC path parameter to be sent to MPC */
  MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding, Scalar qpFactor);
}
