// code by az and jph
package ch.ethz.idsc.demo.az;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.seye.SeyeAeImuLcmClient;
import ch.ethz.idsc.retina.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.davis.io.Aedat31Imu6Event;

/* package */ enum SeyeAeImuDemo {
  ;
  public static void main(String[] args) throws InterruptedException {
    SeyeAeImuLcmClient seyeAeImuLcmHandler = new SeyeAeImuLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    seyeAeImuLcmHandler.aedat31Imu6Listeners.add(new Aedat31Imu6Listener() {
      @Override
      public void imu6Event(Aedat31Imu6Event aedat31Imu6Event) {
        // if (aedat31Imu6Event.isValid())
        {
          System.out.println("Temp: " + aedat31Imu6Event.getTemperature());
          System.out.println("Temp: " + aedat31Imu6Event.getAccel());
        }
      }
    });
    seyeAeImuLcmHandler.startSubscriptions();
    Thread.sleep(10000);
  }
}
