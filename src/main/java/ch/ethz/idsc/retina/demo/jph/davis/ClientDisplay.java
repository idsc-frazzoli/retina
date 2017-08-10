// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.IOException;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisEventProvider;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.DavisEventViewer;
import ch.ethz.idsc.retina.davis.io.aedat.AedatClientProvider;

enum ClientDisplay {
  ;
  public static void main(String[] args) throws IOException {
    DavisDecoder davisDecoder = Davis240c.INSTANCE.createDecoder();
    DavisEventProvider davisEventProvider = new AedatClientProvider(davisDecoder);
    DavisEventViewer.of(davisEventProvider, davisDecoder, Davis240c.INSTANCE, -1);
  }
}
