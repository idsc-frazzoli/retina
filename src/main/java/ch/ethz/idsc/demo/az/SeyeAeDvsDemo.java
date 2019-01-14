// code by az and jph
package ch.ethz.idsc.demo.az;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.jph.davis.Aedat31PolarityImage;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.seye.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum SeyeAeDvsDemo {
  ;
  public static void main(String[] args) throws InterruptedException {
    SeyeAeDvsLcmClient seyeAeDvsLcmClient = new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    // siliconEyeLcmHandler.aedat31Imu6Listeners.add(new Aedat31Imu6Listener() {
    // @Override
    // public void imu6Event(Aedat31Imu6Event aedat31Imu6Event) {
    // System.out.println("Temp: " + aedat31Imu6Event.getTemperature());
    // }
    // });
    Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(Color.BLACK, 2500);
    aedat31PolarityImage.listeners.add(new TimedImageListener() {
      int count = 0;

      @Override
      public void timedImage(TimedImageEvent timedImageEvent) {
        System.out.println(timedImageEvent.time);
        File img = HomeDirectory.Pictures("img" + count + ".png");
        try {
          ImageIO.write(timedImageEvent.bufferedImage, "png", img);
        } catch (IOException e) {
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
