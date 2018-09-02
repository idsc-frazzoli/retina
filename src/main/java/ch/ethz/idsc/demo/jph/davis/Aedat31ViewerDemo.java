// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.tensor.io.AnimatedGifWriter;

enum Aedat31ViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    try (AnimatedGifWriter agw2 = AnimatedGifWriter.of(UserHome.file("events.gif"), 100)) {
      Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(8);
      aedat31PolarityImage.listeners.add(new TimedImageListener() {
        int count = 0;

        @Override
        public void timedImage(TimedImageEvent timedImageEvent) {
          try {
            // System.out.println("here " + count);
            if (60000 < count && count < 61000)
              agw2.append(timedImageEvent.bufferedImage);
            ++count;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
      // davisDecoder.addPolarityListener(aedat31PolarityImage);
      try (AnimatedGifWriter agw = AnimatedGifWriter.of(UserHome.file("camera.gif"), 100)) {
        Aedat31FileSupplier davisEventProvider = //
            new Aedat31FileSupplier(Aedat31.LOG_04.file);
        davisEventProvider.aedat31PolarityListeners.add(aedat31PolarityImage);
        davisEventProvider.start();
        System.out.println("done");
      }
    }
  }
}
