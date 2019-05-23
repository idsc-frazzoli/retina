// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class Vmu931AccRow extends GokartLogImageRow implements Vmu931ImuFrameListener {
  private static final Clip CLIP = Clips.positive(Vmu931LcmServerModule.VMU931_G.clip().max());
  // ---
  private final int index;
  private Scalar acc = RealScalar.ZERO;

  public Vmu931AccRow(int index) {
    this.index = index;
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    acc = Max.of(acc, CLIP.rescale(vmu931ImuFrame.acceleration().Get(index).abs()));
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = acc;
    acc = RealScalar.ZERO;
    return value;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.JET;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "vmu931 acc " + index;
  }
}
