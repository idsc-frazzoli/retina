// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class PoseQualityRow extends ClipLogImageRow implements GokartPoseListener {
  private static final Clip CLIP = Clips.interval(0.5, 1);
  private Scalar scalar = RealScalar.ZERO;

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    scalar = CLIP.rescale(gokartPoseEvent.getQuality());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.AVOCADO;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "pose quality";
  }

  @Override
  public Clip clip() {
    return CLIP;
  }
}
