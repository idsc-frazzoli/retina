// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;

/** listens to {@link RimoGetEvent}s and passes them to
 * the {@link GokartPoseOdometry} */
// TODO architecture not ideal: should listen directly to socket?
/* package */ class OdometryRimoGetLcmClient extends BinaryLcmClient {
  public final GokartPoseOdometry gokartPoseOdometry;

  public OdometryRimoGetLcmClient() {
    gokartPoseOdometry = GokartPoseOdometry.create();
  }

  @Override // from LcmClientAdapter
  protected void messageReceived(ByteBuffer byteBuffer) {
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    gokartPoseOdometry.getEvent(rimoGetEvent);
  }

  @Override // from LcmClientAdapter
  protected String channel() {
    return RimoLcmServer.CHANNEL_GET;
  }
}
