// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/** transforms events from image plane to go kart frame */
/* package */ class SlamImageToGokart extends AbstractSlamStep {
  private final ImageToGokartInterface imageToGokartInterface = //
      SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.createImageToGokartInterface();

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
    slamCoreContainer.setEventGokartFrame(SlamImageToGokartUtil.checkEventPosition(eventGokartFrame)//
        ? null
        : eventGokartFrame);
  }
}
