// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;

/* package */ enum SiliconEyeVideo {
  ;
  public static void main(String[] args) throws Exception {
    /** Read in some option values and their defaults. */
    final int snaps = 25; // fps
    final String filename = UserHome.file("siliconeye.mp4").toString();
    File file = new File("/media/datahaki/backup/siliconeye/20180830", //
        "sees_control_recording_2018_08_30-13_45_50.aedat");
    Aedat31FileSupplier aedat31FileSupplier = new Aedat31FileSupplier(file);
    // ---
    Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(16);
    aedat31FileSupplier.aedat31PolarityListeners.add(aedat31PolarityImage);
    // ---
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(filename, SiliconEyeVideoFrame.DIMENSION, snaps)) {
      SiliconEyeVideoFrame videoImageRender = new SiliconEyeVideoFrame(new TimedImageListener() {
        @Override
        public void timedImage(TimedImageEvent timedImageEvent) {
          if (5000 < timedImageEvent.time) {
            mp4.append(timedImageEvent.bufferedImage);
            if (5000 + 500 * 30 < timedImageEvent.time)
              aedat31FileSupplier.stop();
          }
        }
      });
      aedat31FileSupplier.aedat31FrameListeners.add(videoImageRender);
      aedat31PolarityImage.listeners.add(videoImageRender);
      aedat31FileSupplier.start();
    } // close video
  }
}
