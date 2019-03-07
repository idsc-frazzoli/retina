// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

import ch.ethz.idsc.retina.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum SiliconEyeVideo {
  ;
  public static void main(String[] args) throws Exception {
    /** Read in some option values and their defaults. */
    final int snaps = 32; // fps
    final String filename = HomeDirectory.file("siliconeye.mp4").toString();
    File file = new File("/media/datahaki/backup/siliconeye/20180830", //
        "sees_control_recording_2018_08_30-13_45_50.aedat");
    Aedat31FileSupplier aedat31FileSupplier = new Aedat31FileSupplier(file);
    // ---
    Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(Color.BLACK, 250);
    aedat31FileSupplier.aedat31PolarityListeners.add(aedat31PolarityImage);
    // ---
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(filename, SiliconEyeVideoFrame.DIMENSION, snaps)) {
      SiliconEyeVideoFrame videoImageRender = new SiliconEyeVideoFrame(new Consumer<BufferedImage>() {
        int count = 0;

        @Override
        public void accept(BufferedImage bufferedImage) {
          System.out.println(count);
          ++count;
          if (snaps * 10 < count) {
            mp4.append(bufferedImage);
            if (snaps * (10 + 120) < count) {
              System.out.println("stop");
              aedat31FileSupplier.stop();
            }
          }
        }
      });
      aedat31FileSupplier.aedat31FrameListeners.add(videoImageRender);
      aedat31PolarityImage.listeners.add(videoImageRender);
      aedat31FileSupplier.start();
    } // close video
  }
}
