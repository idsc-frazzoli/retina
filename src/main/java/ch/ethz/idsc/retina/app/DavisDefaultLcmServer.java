// code by jph
package ch.ethz.idsc.retina.app;

import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.data.DavisApsBlockCollector;
import ch.ethz.idsc.retina.davis.data.DavisApsBlockListener;
import ch.ethz.idsc.retina.davis.data.DavisApsColumnCompiler;
import ch.ethz.idsc.retina.davis.data.DavisApsDatagramServer;
import ch.ethz.idsc.retina.davis.data.DavisDvsBlockCollector;
import ch.ethz.idsc.retina.davis.data.DavisDvsBlockListener;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramServer;

/** collection of functionality that filters raw data for aps content
 * the aps content is encoded in timed column blocks and sent via {@link DavisApsDatagramServer}
 * the dvs content is encoded in packets with at most 300 events and sent via {@link DavisDvsDatagramServer} */
public enum DavisDefaultLcmServer {
  INSTANCE;
  // ---
  public final DavisDecoder davisDecoder;

  private DavisDefaultLcmServer() {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    // ---
    DavisDvsBlockCollector davisDvsBlockCollector = new DavisDvsBlockCollector();
    DavisDvsBlockListener davisDvsBlockListener = new DavisDvsBlockPublisher();
    davisDvsBlockCollector.setListener(davisDvsBlockListener);
    davisDecoder.addListener(davisDvsBlockCollector);
    // ---
    DavisApsBlockListener davisApsBlockListener = new DavisApsBlockPublisher();
    DavisApsBlockCollector davisApsBlockCollector = new DavisApsBlockCollector(8);
    davisApsBlockCollector.setListener(davisApsBlockListener);
    DavisApsColumnCompiler davisApsColumnCompiler = new DavisApsColumnCompiler(davisApsBlockCollector);
    davisDecoder.addListener(davisApsColumnCompiler);
    // ---
    // ImuDatagramServer imuDatagramServer = new ImuDatagramServer();
    // DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
    // davisImuFrameCollector.addListener(imuDatagramServer);
    // davisDecoder.addListener(davisImuFrameCollector);
  }

  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }
}
