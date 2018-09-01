// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31Decoder;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31FrameEvent;
import ch.ethz.idsc.retina.dev.davis.seye.SiliconEyeDecoder;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;

/* package */ enum SiliconEyeVideo {
  ;
  static final int WIDTH = 320;

  public static void main(String[] args) throws Exception {
    /** Read in some option values and their defaults. */
    final int snaps = 15; // fps
    final String filename = UserHome.file("filename2.mp4").toString();
    Dimension dimension = new Dimension(WIDTH * 2, 264);
    BufferedImage bufferedImage = //
        new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    Aedat31Decoder davisDecoder = new SiliconEyeDecoder();
    Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage();
    // ---
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(filename, dimension, snaps)) {
      aedat31PolarityImage.listeners.add(new TimedImageListener() {
        int count = 0;

        @Override
        public void timedImage(TimedImageEvent timedImageEvent) {
          if (count % 1000 == 0)
            System.out.println("here " + count);
          graphics.drawImage(timedImageEvent.bufferedImage, WIDTH, 0, null);
          String string = "" + timedImageEvent.time + "[us]";
          graphics.setColor(Color.BLACK);
          graphics.drawString(string, WIDTH + 1, 12);
          graphics.setColor(Color.WHITE);
          graphics.drawString(string, WIDTH, 11);
          if (00000 < count && count < 10000000)
            mp4.append(bufferedImage);
          ++count;
        }
      });
      davisDecoder.addPolarityListener(aedat31PolarityImage);
      // AnimatedGifWriter agw = AnimatedGifWriter.of(UserHome.file("camera.gif"), 100);
      File file = new File("/media/datahaki/backup/siliconeye/20180830", //
          "sees_control_recording_2018_08_30-13_45_50.aedat");
      Aedat31FileSupplier davisEventProvider = new Aedat31FileSupplier(file);
      davisEventProvider.aedat31PolarityListeners.add(aedat31PolarityImage);
      davisEventProvider.aedat31FrameListeners.add(new Aedat31FrameListener() {
        @Override
        public void frameEvent(Aedat31FrameEvent aedat31FrameEvent) {
          aedat31PolarityImage.setBackground(aedat31FrameEvent.getBufferedImage());
          graphics.drawImage(aedat31FrameEvent.getBufferedImage(), 0, 0, null);
          try {
            // System.out.println("append " + aedat31FrameEvent.getBufferedImage());
            // agw.append(aedat31FrameEvent.getBufferedImage());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      davisEventProvider.start();
    } // close video
  }
}
