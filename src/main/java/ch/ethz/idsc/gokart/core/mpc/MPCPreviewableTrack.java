// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Tensor;

public interface MPCPreviewableTrack {
  MPCPathParameter getPathParameterPreview(int previewSize, Tensor PositionW);
}
