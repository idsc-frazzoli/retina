// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.Tensor;

public class SampledTrackBoundaries implements TrackBoundaries {
  private final Tensor middle;
  private final Tensor lineL;
  private final Tensor lineR;

  public SampledTrackBoundaries(Tensor middle, Tensor lineL, Tensor lineR) {
    this.middle = middle;
    this.lineL = lineL;
    this.lineR = lineR;
  }

  @Override // from TrackBoundaries
  public Tensor getLineCenter() {
    return middle;
  }

  @Override // from TrackBoundaries
  public Tensor getLineLeft() {
    return lineL;
  }

  @Override // from TrackBoundaries
  public Tensor getLineRight() {
    return lineR;
  }
}
