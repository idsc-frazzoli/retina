// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.retina.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum Aedat31ViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    try (AnimationWriter agw2 = new GifAnimationWriter(HomeDirectory.file("events.gif"), 100, TimeUnit.MILLISECONDS)) {
      Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(Color.BLACK, 8);
      aedat31PolarityImage.listeners.add(new TimedImageListener() {
        int count = 0;

        @Override
        public void timedImage(TimedImageEvent timedImageEvent) {
          try {
            // System.out.println("here " + count);
            if (60_000 < count && count < 61_000)
              agw2.write(timedImageEvent.bufferedImage);
            ++count;
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      });
      // davisDecoder.addPolarityListener(aedat31PolarityImage);
      try (AnimationWriter agw = new GifAnimationWriter(HomeDirectory.file("camera.gif"), 100, TimeUnit.MILLISECONDS)) {
        Aedat31FileSupplier davisEventProvider = //
            new Aedat31FileSupplier(Aedat31.LOG_04.file);
        davisEventProvider.aedat31PolarityListeners.add(aedat31PolarityImage);
        davisEventProvider.start();
        System.out.println("done");
      }
    }
  }
}
