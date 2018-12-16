// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** wrapper class around {@link GokartPoseLcmServer} */
public class GokartPoseLcmModule extends AbstractClockedModule {
  private static final Scalar PERIOD = Quantity.of(50, SI.PER_SECOND).reciprocal();
  // ---

  @Override // from AbstractModule
  protected void first() throws Exception {
    GokartPoseLcmServer.INSTANCE.odometryRimoGetLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    GokartPoseLcmServer.INSTANCE.odometryRimoGetLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    // TODO the pose server publishes pose values even when the pose is not initialized (in that case the quality == 0)
    GokartPoseLcmServer.INSTANCE.publish();
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PERIOD;
  }
}
