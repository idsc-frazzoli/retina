// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.LcmClientAdapter;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;

public class OdometryLcmClient extends LcmClientAdapter {
  public final GokartOdometry gokartOdometry = new GokartOdometry();

  @Override // from LcmClientAdapter
  protected String channel() {
    return RimoLcmServer.CHANNEL_GET;
  }

  @Override // from LcmClientAdapter
  protected void messageReceived(ByteBuffer byteBuffer) {
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    gokartOdometry.getEvent(rimoGetEvent);
  }

  public static void main(String[] args) throws Exception {
    OdometryLcmClient odometryLcmClient = new OdometryLcmClient();
    odometryLcmClient.startSubscriptions();
    Thread.sleep(10000);
  }
}
