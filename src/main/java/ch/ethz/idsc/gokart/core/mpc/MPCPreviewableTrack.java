// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// TODO MH document functions
/* package */ interface MPCPreviewableTrack {
  MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding);

  MPCPathParameter getPathParameterPreview(int previewSize, Tensor position, Scalar padding, Scalar qpFactor);
}
