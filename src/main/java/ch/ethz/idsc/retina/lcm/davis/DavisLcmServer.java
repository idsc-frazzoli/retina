// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import ch.ethz.idsc.retina.dev.davis.DavisApsListener;
import ch.ethz.idsc.retina.dev.davis.DavisApsType;
import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dev.davis.app.ResetDavisApsCorrection;
import ch.ethz.idsc.retina.dev.davis.data.CorrectedDavisApsColumnCompiler;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockListener;
import ch.ethz.idsc.retina.dev.davis.data.DavisApsColumnCompiler;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockCollector;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockListener;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameCollector;

/** collection of functionality that filters raw data for aps content the aps
 * content is encoded in timed column blocks and sent via
 * DavisApsDatagramServer the dvs content is encoded in packets with at
 * most 300 events and sent via DavisDvsDatagramServer the imu content
 * is encoded as {@link DavisImu}
 * 
 * <p>
 * tested on cameras:
 * 
 * <pre>
 * DAVIS FX2 02460045
 * </pre> */
public class DavisLcmServer {
  // ---
  public final DavisDecoder davisDecoder;

  /** @param serial for instance "FX2_02460045"
   * @param cameraId determines the channel name "davis240c.cameraId.aps", "davis240c.cameraId.imu"
   * @param davisApsTypes */
  public DavisLcmServer(String serial, String cameraId, DavisApsType... davisApsTypes) {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    {
      DavisDvsBlockCollector davisDvsBlockCollector = new DavisDvsBlockCollector();
      DavisDvsBlockListener davisDvsBlockListener = new DavisDvsBlockPublisher(cameraId);
      davisDvsBlockCollector.setListener(davisDvsBlockListener);
      davisDecoder.addDvsListener(davisDvsBlockCollector);
    }
    Set<DavisApsType> set = EnumSet.copyOf(Arrays.asList(davisApsTypes));
    if (set.contains(DavisApsType.RST))
      davisDecoder.addRstListener(create(cameraId, DavisApsType.RST)); // RST
    if (set.contains(DavisApsType.SIG))
      davisDecoder.addSigListener(create(cameraId, DavisApsType.SIG)); // SIG
    if (set.contains(DavisApsType.DIF)) {
      ResetDavisApsCorrection resetDavisApsCorrection = new ResetDavisApsCorrection();
      davisDecoder.addRstListener(resetDavisApsCorrection);
      DavisApsBlockListener davisApsBlockListener = new DavisApsBlockPublisher(cameraId, DavisApsType.SIG);
      DavisApsBlockCollector davisApsBlockCollector = new DavisApsBlockCollector();
      davisApsBlockCollector.setListener(davisApsBlockListener);
      DavisApsListener davisApsColumnCompiler = new CorrectedDavisApsColumnCompiler(davisApsBlockCollector, resetDavisApsCorrection);
      davisDecoder.addSigListener(davisApsColumnCompiler);
    }
    {
      DavisImuFramePublisher davisImuFramePublisher = new DavisImuFramePublisher(cameraId);
      DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
      davisImuFrameCollector.addListener(davisImuFramePublisher);
      davisDecoder.addImuListener(davisImuFrameCollector);
    }
  }

  /** @param length
   * @param data
   * @param time */
  public void append(int length, int[] data, int[] time) {
    for (int index = 0; index < length; ++index)
      davisDecoder.read(data[index], time[index]);
  }

  private static DavisApsListener create(String cameraId, DavisApsType davisApsType) {
    DavisApsBlockListener davisApsBlockListener = new DavisApsBlockPublisher(cameraId, davisApsType);
    DavisApsBlockCollector davisApsBlockCollector = new DavisApsBlockCollector();
    davisApsBlockCollector.setListener(davisApsBlockListener);
    return new DavisApsColumnCompiler(davisApsBlockCollector);
  }
}
