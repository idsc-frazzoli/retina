// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.BSplineTrackListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class BSplineTrackRow extends GokartLogImageRow implements BSplineTrackListener {
  private static final Clip CLIP = Clips.positive(1);
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from BSplineTrackListener
  public void bSplineTrack(Optional<BSplineTrack> optional) {
    if (optional.isPresent())
      scalar = scalar.add(RealScalar.ONE);
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = CLIP.rescale(scalar);
    scalar = RealScalar.ZERO;
    return value;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.SUNSET;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "bspline track";
  }
}
