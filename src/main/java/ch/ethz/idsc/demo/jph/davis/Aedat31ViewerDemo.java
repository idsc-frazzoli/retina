// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.IOException;
import java.util.Map.Entry;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31Decoder;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31FileSupplier;
import ch.ethz.idsc.retina.dev.davis.seye.SiliconEyeDecoder;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.AnimatedGifWriter;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** playback of aedat log file and visualization of content. data processing is
 * restricted to dvs event accumulation */
enum Aedat31ViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    Aedat31Decoder davisDecoder = new SiliconEyeDecoder();
    AnimatedGifWriter agw2 = AnimatedGifWriter.of(UserHome.file("events.gif"), 100);
    Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage();
    aedat31PolarityImage.listeners.add(new TimedImageListener() {
      int count = 0;

      @Override
      public void timedImage(TimedImageEvent timedImageEvent) {
        try {
          System.out.println("here " + count);
          if (60000 < count && count < 61000)
            agw2.append(timedImageEvent.bufferedImage);
          ++count;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    davisDecoder.addPolarityListener(aedat31PolarityImage);
    AnimatedGifWriter agw = AnimatedGifWriter.of(UserHome.file("camera.gif"), 100);
    Aedat31FileSupplier davisEventProvider = //
        new Aedat31FileSupplier(Aedat.LOG_04.file, davisDecoder);
    davisEventProvider.dvsPolarityListeners.add(aedat31PolarityImage);
    davisEventProvider.start();
    agw.close();
    agw2.close();
    // System.out.println(davisEventProvider.map);
    TableBuilder tableBuilder = new TableBuilder();
    for (Entry<Integer, Integer> entry : davisEventProvider.map.entrySet()) {
      tableBuilder.appendRow(Tensors.vector(entry.getKey(), entry.getValue()));
    }
    Export.of(UserHome.file("table.csv"), tableBuilder.toTable());
    // davisEventProvider.
    // DavisEventViewer.of(davisEventProvider, davisDecoder, Davis240c.INSTANCE, 1.0);
  }
}
