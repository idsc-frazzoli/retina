// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.io.aps.ApsBlockCollector;
import ch.ethz.idsc.retina.davis.io.aps.ApsColumnCompiler;
import ch.ethz.idsc.retina.davis.io.aps.ApsDatagramServer;
import ch.ethz.idsc.retina.davis.io.dvs.DvsBlockCollector;
import ch.ethz.idsc.retina.davis.io.dvs.DvsDatagramServer;

/** collection of functionality that filters raw data for aps content
 * the aps content is encoded in timed column blocks and sent via {@link ApsDatagramServer}
 * the dvs content is encoded in packets with at most 300 events and sent via {@link DvsDatagramServer} */
public enum DavisDefaultDatagramServer {
  INSTANCE;
  // ---
  public final DavisDecoder davisDecoder;

  @SuppressWarnings("unused")
  private DavisDefaultDatagramServer() {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    // ---
    ApsBlockCollector apsBlockCollector = new ApsBlockCollector(8);
    ApsDatagramServer apsStandaloneServer = new ApsDatagramServer(apsBlockCollector);
    ApsColumnCompiler apsColumnCompiler = new ApsColumnCompiler(apsBlockCollector);
    davisDecoder.addListener(apsColumnCompiler);
    // ---
    DvsBlockCollector dvsBlockCollector = new DvsBlockCollector();
    DvsDatagramServer dvsStandaloneServer = new DvsDatagramServer(dvsBlockCollector);
    davisDecoder.addListener(dvsBlockCollector);
  }

  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }
}
