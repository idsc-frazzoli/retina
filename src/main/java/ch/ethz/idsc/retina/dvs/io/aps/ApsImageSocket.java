// code by jph
package ch.ethz.idsc.retina.dvs.io.aps;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dvs.io.dvs.DvsBlockCollector;
import ch.ethz.idsc.retina.dvs.io.dvs.DvsStandaloneServer;

/** collection of functionality that filters raw data for aps content
 * the aps content is encoded in timed column blocks and sent via UDP */
public enum ApsImageSocket {
  INSTANCE;
  // ---
  public final DavisDecoder davisDecoder;

  @SuppressWarnings("unused")
  private ApsImageSocket() {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    // ---
    ApsBlockCollector apsBlockCollector = new ApsBlockCollector(8);
    ApsStandaloneServer apsStandaloneServer = new ApsStandaloneServer(apsBlockCollector);
    ApsColumnCompiler apsColumnCompiler = new ApsColumnCompiler(apsBlockCollector);
    davisDecoder.addListener(apsColumnCompiler);
    // ---
    DvsBlockCollector dvsBlockCollector = new DvsBlockCollector();
    DvsStandaloneServer dvsStandaloneServer = new DvsStandaloneServer(dvsBlockCollector);
    davisDecoder.addListener(dvsBlockCollector);
  }

  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }
}
