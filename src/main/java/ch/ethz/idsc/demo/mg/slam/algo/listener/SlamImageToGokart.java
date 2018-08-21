// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartInterface;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** transforms events to go kart frame */
/* package */ class SlamImageToGokart implements DavisDvsListener {
  private final ImageToGokartInterface imageToGokartInterface;
  private final double lookAheadDistance;
  // ---
  private double[] eventGokartFrame;

  SlamImageToGokart(SlamConfig slamConfig) {
    imageToGokartInterface = slamConfig.davisConfig.createImageToGokartUtilLookup();
    lookAheadDistance = Magnitude.METER.toDouble(slamConfig.lookAheadDistance);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    eventGokartFrame = imageToGokartInterface.imageToGokart(davisDvsEvent.x, davisDvsEvent.y);
  }

  /** @return null if eventGokartFrame[0] > lookaheadDistance */
  public double[] getEventGokartFrame() {
    return eventGokartFrame[0] > lookAheadDistance ? null : eventGokartFrame;
  }
}
