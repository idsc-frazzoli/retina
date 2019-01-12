// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.DavisEventViewer;
import ch.ethz.idsc.retina.davis.io.Aedat20FileSupplier;
import ch.ethz.idsc.retina.util.StartAndStoppable;

/** playback of aedat log file and visualization of content. data processing is
 * restricted to dvs event accumulation */
enum Aedat20ViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    StartAndStoppable davisEventProvider = //
        new Aedat20FileSupplier(Aedat20.LOG_01.file, davisDecoder);
    DavisEventViewer.of(davisEventProvider, davisDecoder, Davis240c.INSTANCE, 1.0);
  }
}
