// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class SteerAngleRow extends GokartLogImageRow implements SteerColumnListener {
  private final Scalar limit = SteerPutEvent.ENCODER.apply(SteerConfig.GLOBAL.columnMax);
  private final Clip clip = Clips.absolute(limit.number().doubleValue());
  private Scalar scalar = RealScalar.ZERO;

  @Override
  public void getEvent(SteerColumnEvent steerColumnInterface) {
    scalar = steerColumnInterface.isSteerColumnCalibrated() //
        ? clip.rescale(SteerPutEvent.ENCODER.apply(steerColumnInterface.getSteerColumnEncoderCentered()))
        : RealScalar.ZERO;
  }

  @Override
  public Scalar getScalar() {
    return scalar;
  }

  @Override
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.THERMOMETER;
  }

  @Override
  public String getName() {
    return "steer angle";
  }
}
