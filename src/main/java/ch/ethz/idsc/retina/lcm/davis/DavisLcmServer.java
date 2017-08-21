// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import ch.ethz.idsc.retina.dev.davis.DavisApsType;
import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240cDecoder;
import ch.ethz.idsc.retina.dev.davis.app.DavisApsCorrection;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockListener;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsColumnCompiler;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsCorrectedColumnCompiler;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockListener;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramServer;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameCollector;
import idsc.DavisImu;

/** collection of functionality that filters raw data for aps content
 * the aps content is encoded in timed column blocks and sent via {@link DavisApsDatagramServer}
 * the dvs content is encoded in packets with at most 300 events and sent via {@link DavisDvsDatagramServer}
 * the imu content is encoded as {@link DavisImu}
 * 
 * <p>tested on cameras:
 * <pre>
 * DAVIS FX2 02460045
 * </pre> */
public class DavisLcmServer {
  // ---
  public final DavisDecoder davisDecoder;

  /** @param serial for instance "FX2_02460045"
   * @param cameraId */
  public DavisLcmServer(String serial, String cameraId) {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    {
      DavisDvsBlockCollector davisDvsBlockCollector = new DavisDvsBlockCollector();
      DavisDvsBlockListener davisDvsBlockListener = new DavisDvsBlockPublisher(cameraId);
      davisDvsBlockCollector.setListener(davisDvsBlockListener);
      davisDecoder.addListener(davisDvsBlockCollector);
    }
    {
      DavisApsBlockListener davisApsBlockListener = new DavisApsBlockPublisher(cameraId, DavisApsType.IMG);
      DavisApsBlockCollector davisApsBlockCollector = new DavisApsBlockCollector(8);
      davisApsBlockCollector.setListener(davisApsBlockListener);
      DavisApsCorrection davisApsCorrection = new DavisApsCorrection(serial);
      DavisApsColumnCompiler davisApsColumnCompiler = //
          new DavisApsCorrectedColumnCompiler(davisApsBlockCollector, davisApsCorrection);
      davisDecoder.addListener(davisApsColumnCompiler);
    }
    {
      DavisApsBlockListener davisApsBlockListener = new DavisApsBlockPublisher(cameraId, DavisApsType.RST);
      DavisApsBlockCollector davisApsBlockCollector = new DavisApsBlockCollector(8);
      davisApsBlockCollector.setListener(davisApsBlockListener);
      DavisApsCorrection davisApsCorrection = new DavisApsCorrection(serial);
      DavisApsColumnCompiler davisApsColumnCompiler = //
          new DavisApsCorrectedColumnCompiler(davisApsBlockCollector, davisApsCorrection);
      Davis240cDecoder d = (Davis240cDecoder) davisDecoder;
      d.addRstListener(davisApsColumnCompiler);
    }
    {
      DavisImuFramePublisher davisImuFramePublisher = new DavisImuFramePublisher(cameraId);
      DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
      davisImuFrameCollector.addListener(davisImuFramePublisher);
      davisDecoder.addListener(davisImuFrameCollector);
    }
  }

  /** @param length
   * @param data
   * @param time */
  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }
}
