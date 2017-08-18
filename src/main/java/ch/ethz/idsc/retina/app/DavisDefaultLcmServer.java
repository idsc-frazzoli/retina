// code by jph
package ch.ethz.idsc.retina.app;

import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockListener;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsColumnCompiler;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockListener;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameCollector;

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
    DavisImuFramePublisher davisImuFramePublisher = new DavisImuFramePublisher();
    DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
    davisImuFrameCollector.addListener(davisImuFramePublisher);
    davisDecoder.addListener(davisImuFrameCollector);
  }

  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }
}
