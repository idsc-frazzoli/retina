// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;

public class SiliconEyeImuLcmHandler extends BinaryLcmClient {
  public final List<Aedat31Imu6Listener> aedat31Imu6Listeners = new LinkedList<>();

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    int events = byteBuffer.remaining() / 40;
    for (int count = 0; count < events; ++count) {
      Aedat31Imu6Event aedat31ImuEvent = new Aedat31Imu6Event(byteBuffer);
      aedat31Imu6Listeners.forEach(listener -> listener.imu6Event(aedat31ImuEvent));
    }
  }

  @Override
  protected String channel() {
    return "se_rino3.overview.aeimu";
  }

  public static void main(String[] args) throws InterruptedException {
    SiliconEyeImuLcmHandler siliconEyeLcmHandler = new SiliconEyeImuLcmHandler();
    siliconEyeLcmHandler.aedat31Imu6Listeners.add(new Aedat31Imu6Listener() {
      @Override
      public void imu6Event(Aedat31Imu6Event aedat31Imu6Event) {
        // if (aedat31Imu6Event.isValid())
        {
          System.out.println("Temp: " + aedat31Imu6Event.getTemperature());
          System.out.println("Temp: " + aedat31Imu6Event.getAccel());
        }
      }
    });
    siliconEyeLcmHandler.startSubscriptions();
    Thread.sleep(10000);
  }
}
