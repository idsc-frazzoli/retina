// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import ch.ethz.idsc.retina.davis.DavisApsListener;
import ch.ethz.idsc.retina.davis.DavisApsType;
import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.ResetDavisApsCorrection;
import ch.ethz.idsc.retina.davis.data.CorrectedDavisApsColumnCompiler;
import ch.ethz.idsc.retina.davis.data.DavisApsBlockCollector;
import ch.ethz.idsc.retina.davis.data.DavisApsColumnCompiler;
import ch.ethz.idsc.retina.davis.data.DavisDvsBlockCollector;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameCollector;

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
  private final DavisDecoder davisDecoder;

  /** @param cameraId determines the channel name "davis240c.cameraId.aps", "davis240c.cameraId.imu"
   * @param davisApsTypes */
  public DavisLcmServer(String cameraId, DavisApsType... davisApsTypes) {
    davisDecoder = Davis240c.INSTANCE.createDecoder();
    // ---
    davisDecoder.addDvsListener(new DavisDvsBlockCollector(new DavisDvsBlockPublisher(cameraId)));
    // ---
    Set<DavisApsType> set = EnumSet.copyOf(Arrays.asList(davisApsTypes));
    if (set.contains(DavisApsType.RST))
      davisDecoder.addRstListener(create(cameraId, DavisApsType.RST)); // RST
    if (set.contains(DavisApsType.SIG))
      davisDecoder.addSigListener(create(cameraId, DavisApsType.SIG)); // SIG
    if (set.contains(DavisApsType.DIF)) {
      ResetDavisApsCorrection resetDavisApsCorrection = new ResetDavisApsCorrection();
      davisDecoder.addRstListener(resetDavisApsCorrection);
      DavisApsBlockCollector davisApsBlockCollector = //
          new DavisApsBlockCollector(new DavisApsBlockPublisher(cameraId, DavisApsType.SIG));
      DavisApsListener davisApsColumnCompiler = //
          new CorrectedDavisApsColumnCompiler(davisApsBlockCollector, resetDavisApsCorrection);
      davisDecoder.addSigListener(davisApsColumnCompiler);
    }
    {
      DavisImuFramePublisher davisImuFramePublisher = new DavisImuFramePublisher(cameraId);
      DavisImuFrameCollector davisImuFrameCollector = new DavisImuFrameCollector();
      davisImuFrameCollector.addListener(davisImuFramePublisher);
      davisDecoder.addImuListener(davisImuFrameCollector);
    }
  }

  /** function called from jaer with fields from AEPacketRaw
   * <pre>
   * append(aeRaw.getNumEvents(), aeRaw.addresses, aeRaw.timestamps);
   * </pre>
   * 
   * @param numEvents
   * @param addresses
   * @param timestamps */
  public void append(int numEvents, int[] addresses, int[] timestamps) {
    for (int index = 0; index < numEvents; ++index)
      davisDecoder.read(addresses[index], timestamps[index]);
  }

  // helper function
  private static DavisApsListener create(String cameraId, DavisApsType davisApsType) {
    return new DavisApsColumnCompiler( //
        new DavisApsBlockCollector( //
            new DavisApsBlockPublisher(cameraId, davisApsType)));
  }
}
