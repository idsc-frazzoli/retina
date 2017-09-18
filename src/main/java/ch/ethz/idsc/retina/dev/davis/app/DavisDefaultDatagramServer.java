// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsColumnCompiler;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameCollector;

/** collection of functionality that filters raw data for aps content the aps
 * content is encoded in timed column blocks and sent via
 * {@link DavisApsDatagramServer} the dvs content is encoded in packets with at
 * most 300 events and sent via {@link DavisDvsDatagramServer} */
public enum DavisDefaultDatagramServer {
  INSTANCE;
  // ---
  public final DavisDecoder davisDecoder;

  @SuppressWarnings("unused")
  private DavisDefaultDatagramServer() {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    // ---
    DavisDvsBlockCollector davisDvsBlockCollector = new DavisDvsBlockCollector();
    DavisDvsDatagramServer davisDvsDatagramServer = new DavisDvsDatagramServer(davisDvsBlockCollector);
    davisDecoder.addDvsListener(davisDvsBlockCollector);
    // ---
    DavisApsBlockCollector davisApsBlockCollector = new DavisApsBlockCollector();
    DavisApsDatagramServer davisApsDatagramServer = new DavisApsDatagramServer(davisApsBlockCollector);
    DavisApsColumnCompiler davisApsColumnCompiler = new DavisApsColumnCompiler(davisApsBlockCollector);
    davisDecoder.addSigListener(davisApsColumnCompiler);
    // ---
    DavisImuDatagramServer davisImuDatagramServer = new DavisImuDatagramServer();
    DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
    davisImuFrameCollector.addListener(davisImuDatagramServer);
    davisDecoder.addImuListener(davisImuFrameCollector);
  }

  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }
}
