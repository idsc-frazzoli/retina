// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.retina.sys.AbstractModule;

public class GokartPoseLcmModule extends AbstractModule {
  private final Timer timer = new Timer();

  @Override // from AbstractModule
  protected void first() throws Exception {
    GokartPoseLcmServer.INSTANCE.odometryLcmClient.startSubscriptions();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
      }
    }, 100, 50);
  }

  @Override // from AbstractModule
  protected void last() {
    GokartPoseLcmServer.INSTANCE.odometryLcmClient.stopSubscriptions();
    timer.cancel();
  }
}
