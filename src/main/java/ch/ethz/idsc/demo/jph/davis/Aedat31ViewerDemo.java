// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.io.Aedat20FileSupplier;
import ch.ethz.idsc.retina.dev.davis.seye.SiliconEyeDecoder;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** playback of aedat log file and visualization of content. data processing is
 * restricted to dvs event accumulation */
enum Aedat31ViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = new SiliconEyeDecoder();
    StartAndStoppable davisEventProvider = //
        new Aedat20FileSupplier(Aedat.LOG_04.file, davisDecoder);
    davisDecoder.addDvsListener(new DavisDvsListener() {
      @Override
      public void davisDvs(DavisDvsEvent davisDvsEvent) {
        System.out.println(davisDvsEvent);
      }
    });
    davisEventProvider.start();
    // davisEventProvider.
    // DavisEventViewer.of(davisEventProvider, davisDecoder, Davis240c.INSTANCE, 1.0);
  }
}
