package ch.ethz.idsc.retina.dev.davis.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.jph.davis.Aedat31PolarityImage;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.dev.davis.Aedat31Imu6Listener;
import ch.ethz.idsc.retina.dev.davis.Aedat31PolarityListener;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.tensor.io.ImageFormat;

public class SiliconEyeApsLcmHandler extends BinaryLcmClient {
  public final List<Aedat31FrameListener> aedat31FrameListeners = new LinkedList<>();

  int count =0;
  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    BufferedImage d = new BufferedImage(320, 264, BufferedImage.TYPE_BYTE_GRAY);
    //ImageForma/t
    System.out.println("frame re");
//    int events = byteBuffer.remaining() / 40;
//    for (int count = 0; count < events; ++count) {
//      Aedat31FrameEvent aedat31FrameEvent = new Aedat31FrameEvent(byteBuffer);
//      aedat31FrameListeners.forEach(listener -> listener.frameEvent(aedat31FrameEvent));
//    }
  }

  @Override
  protected String channel() {
    return "se_rino3.overview.aeaps";
  }

  public static void main(String[] args) throws InterruptedException {
    SiliconEyeApsLcmHandler siliconEyeLcmHandler = new SiliconEyeApsLcmHandler();
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
