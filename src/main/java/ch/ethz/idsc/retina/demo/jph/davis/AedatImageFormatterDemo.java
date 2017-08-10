// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSupplier;
import ch.ethz.idsc.retina.dvs.io.aps.ApsColumnCollector;
import ch.ethz.idsc.retina.dvs.io.aps.ApsColumnCompiler;

enum AedatImageFormatterDemo {
  ;
  public static void main(String[] args) throws Exception {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    ApsColumnCollector columnApsCollector = new ApsColumnCollector(8);
    columnApsCollector.setListener(() -> {
    });
    davisDecoder.addListener(new ApsColumnCompiler(columnApsCollector));
    DavisEventProvider davisEventProvider = new AedatFileSupplier(Aedat.LOG_04.file, davisDecoder);
    davisEventProvider.start();
  }
}
