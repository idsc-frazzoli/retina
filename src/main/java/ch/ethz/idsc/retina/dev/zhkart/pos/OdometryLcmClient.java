// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.lcm.LcmClientAdapter;

public class OdometryLcmClient extends LcmClientAdapter {
  private final GokartOdometry gokartOdometry = new GokartOdometry();

  @Override // from LcmClientAdapter
  protected String channel() {
    return "autobox.rimo.get";
  }

  @Override // from LcmClientAdapter
  protected void messageReceived(ByteBuffer byteBuffer) {
    RimoGetEvent rimoGetEvent = new RimoGetEvent(byteBuffer);
    gokartOdometry.getEvent(rimoGetEvent);
    // System.out.println(gokartOdometry.getState().map(Round._4));
  }

  public static void main(String[] args) throws Exception {
    OdometryLcmClient odometryLcmClient = new OdometryLcmClient();
    odometryLcmClient.startSubscriptions();
    Thread.sleep(10000);
  }
}
