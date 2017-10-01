// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;

public class GokartStatusLcmModule extends AbstractClockedModule {
  public static final String CHANNEL = "gokart.status.get";
  // ---
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(CHANNEL);

  @Override
  protected void first() throws Exception {
  }

  @Override
  protected void runAlgo() {
    boolean isCalibrated = SteerSocket.INSTANCE.getSteerAngleTracker().isCalibrated();
    float steeringAngle = isCalibrated //
        ? (float) SteerSocket.INSTANCE.getSteerAngleTracker().getSteeringValue()
        : Float.NaN;
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(steeringAngle);
    binaryBlobPublisher.accept(gokartStatusEvent.asArray());
  }

  @Override
  protected double getPeriod() {
    return 0.05;
  }

  @Override
  protected void last() {
  }
}
