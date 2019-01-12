// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;

import ch.ethz.idsc.retina.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.tensor.io.AnimatedGifWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum Aedat31ViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    try (AnimatedGifWriter agw2 = AnimatedGifWriter.of(HomeDirectory.file("events.gif"), 100)) {
      Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(Color.BLACK, 8);
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
      try (AnimatedGifWriter agw = AnimatedGifWriter.of(HomeDirectory.file("camera.gif"), 100)) {
        Aedat31FileSupplier davisEventProvider = //
            new Aedat31FileSupplier(Aedat31.LOG_04.file);
        davisEventProvider.aedat31PolarityListeners.add(aedat31PolarityImage);
        davisEventProvider.start();
        System.out.println("done");
      }
    }
  }
}
