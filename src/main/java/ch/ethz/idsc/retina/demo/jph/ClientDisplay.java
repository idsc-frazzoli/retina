// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventProvider;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dvs.app.DavisEventViewer;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatClientProvider;

enum ClientDisplay {
  ;
  public static void main(String[] args) throws IOException {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    DavisEventProvider davisEventProvider = new AedatClientProvider(davisDecoder);
    DavisEventViewer.of(davisEventProvider, davisDecoder, Davis240c.INSTANCE, -1);
  }
}
