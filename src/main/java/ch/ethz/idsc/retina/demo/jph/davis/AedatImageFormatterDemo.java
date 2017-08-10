// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisEventProvider;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.io.aedat.AedatFileSupplier;
import ch.ethz.idsc.retina.davis.io.aps.ApsBlockCollector;
import ch.ethz.idsc.retina.davis.io.aps.ApsColumnCompiler;

enum AedatImageFormatterDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    ApsBlockCollector columnApsCollector = new ApsBlockCollector(8);
    columnApsCollector.setListener(() -> {
    });
    davisDecoder.addListener(new ApsColumnCompiler(columnApsCollector));
    DavisEventProvider davisEventProvider = new AedatFileSupplier(Aedat.LOG_04.file, davisDecoder);
    davisEventProvider.start();
  }
}
