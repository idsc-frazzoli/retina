// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** transforms events from image plane to go kart frame */
/* package */ class SlamImageToGokart extends AbstractSlamStep {
  private final ImageToGokartInterface imageToGokartInterface;
  private final double lookAheadDistance;

  SlamImageToGokart(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    imageToGokartInterface = slamConfig.davisConfig.createImageToGokartUtilLookup();
    lookAheadDistance = Magnitude.METER.toDouble(slamConfig.lookAheadDistance);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    setEventGokartFrame(imageToGokartInterface.imageToGokart(davisDvsEvent.x, davisDvsEvent.y));
  }

  /** sets eventGokartFrame field in SlamContainer. It is set null if eventGokartFrame[0] > lookAheadDistance */
  private void setEventGokartFrame(double[] eventGokartFrame) {
    slamContainer.setEventGokartFrame(eventGokartFrame[0] > lookAheadDistance ? null : eventGokartFrame);
  }
}
