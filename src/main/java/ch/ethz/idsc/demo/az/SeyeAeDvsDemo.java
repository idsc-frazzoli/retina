// code by az and jph
package ch.ethz.idsc.demo.az;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.jph.davis.Aedat31PolarityImage;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.io.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;

enum SeyeAeDvsDemo {
  ;
  public static void main(String[] args) throws InterruptedException {
    SeyeAeDvsLcmClient seyeAeDvsLcmClient = new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    // siliconEyeLcmHandler.aedat31Imu6Listeners.add(new Aedat31Imu6Listener() {
    // @Override
    // public void imu6Event(Aedat31Imu6Event aedat31Imu6Event) {
    // System.out.println("Temp: " + aedat31Imu6Event.getTemperature());
    // }
    // });
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
    seyeAeDvsLcmClient.addDvsListener(aedat31PolarityImage);
    seyeAeDvsLcmClient.startSubscriptions();
    Thread.sleep(10000);
  }
}
