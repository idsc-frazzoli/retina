// code by az and jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.jph.davis.Aedat31PolarityImage;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.dev.davis.Aedat31PolarityListener;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;

public class SiliconEyeDvsLcmHandler extends BinaryLcmClient {
  public final List<Aedat31PolarityListener> aedat31PolarityListeners = new LinkedList<>();
  public final List<Aedat31Imu6Listener> aedat31Imu6Listeners = new LinkedList<>();

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    int events = byteBuffer.remaining() / 8;
    for (int count = 0; count < events; ++count) {
      Aedat31PolarityEvent aedat31PolarityEvent = Aedat31PolarityEvent.create(byteBuffer);
      aedat31PolarityListeners.forEach(listener -> listener.polarityEvent(aedat31PolarityEvent));
    }
  }

  @Override
  protected String channel() {
    return "se_rino3.overview.aedvs";
  }

  public static void main(String[] args) throws InterruptedException {
    SiliconEyeDvsLcmHandler siliconEyeLcmHandler = new SiliconEyeDvsLcmHandler();
    siliconEyeLcmHandler.aedat31Imu6Listeners.add(new Aedat31Imu6Listener() {
      @Override
      public void imu6Event(Aedat31Imu6Event aedat31Imu6Event) {
        System.out.println("Temp: " + aedat31Imu6Event.getTemperature());
      }
    });
    Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(2500);
    aedat31PolarityImage.listeners.add(new TimedImageListener() {
      int count = 0;

      @Override
      public void timedImage(TimedImageEvent timedImageEvent) {
        System.out.println(timedImageEvent.time);
        File img = UserHome.Pictures("img" + count + ".png");
        try {
          ImageIO.write(timedImageEvent.bufferedImage, "png", img);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        count++;
      }
    });
    siliconEyeLcmHandler.aedat31PolarityListeners.add(aedat31PolarityImage);
    siliconEyeLcmHandler.startSubscriptions();
    Thread.sleep(10000);
  }
}
