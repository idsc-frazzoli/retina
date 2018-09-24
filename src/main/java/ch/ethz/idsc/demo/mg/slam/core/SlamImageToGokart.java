// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** transforms events from image plane to go kart frame */
/* package */ class SlamImageToGokart extends AbstractSlamStep {
  private final ImageToGokartInterface imageToGokartInterface = //
      SlamCoreConfig.GLOBAL.davisConfig.createImageToGokartInterface();
  private final double lookAheadDistance = Magnitude.METER.toDouble(SlamCoreConfig.GLOBAL.lookAheadDistance);

  SlamImageToGokart(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    setEventGokartFrame(imageToGokartInterface.imageToGokart(davisDvsEvent.x, davisDvsEvent.y));
  }

  /** sets eventGokartFrame field in SlamContainer. It is set null if eventGokartFrame[0] > lookAheadDistance.
   * Events which result from objects too far away from the go kart are neglected */
  private void setEventGokartFrame(double[] eventGokartFrame) {
    slamCoreContainer.setEventGokartFrame(eventGokartFrame[0] > lookAheadDistance ? null : eventGokartFrame);
  }
}
