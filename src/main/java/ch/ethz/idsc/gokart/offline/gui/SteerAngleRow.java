// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** uses steer column centered */
/* package */ class SteerAngleRow extends ClipLogImageRow implements SteerColumnListener {
  private final Clip clip = Clips.absolute(SteerConfig.GLOBAL.columnMax);
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from SteerColumnListener
  public void getEvent(SteerColumnEvent steerColumnInterface) {
    scalar = steerColumnInterface.isSteerColumnCalibrated() //
        ? clip.rescale(steerColumnInterface.getSteerColumnEncoderCentered())
        : RealScalar.ZERO;
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.THERMOMETER;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "steer angle";
  }

  @Override
  public Clip clip() {
    return clip;
  }
}
