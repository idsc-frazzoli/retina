// code by az and jph
package ch.ethz.idsc.demo.az;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.seye.SeyeAeApsLcmClient;
import ch.ethz.idsc.retina.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.davis.io.Aedat31FrameEvent;

enum SeyeAeApsDemo {
  ;
  public static void main(String[] args) throws InterruptedException {
    SeyeAeApsLcmClient siliconEyeLcmHandler = new SeyeAeApsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    siliconEyeLcmHandler.aedat31FrameListeners.add(new Aedat31FrameListener() {
      @Override
      public void frameEvent(Aedat31FrameEvent aedat31FrameEvent) {
        System.out.println("received frame");
      }
    });
    siliconEyeLcmHandler.startSubscriptions();
    Thread.sleep(10000);
  }
}
