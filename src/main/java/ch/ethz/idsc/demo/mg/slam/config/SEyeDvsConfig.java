// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.seye.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets DvsConfig parameters according to siliconEye */
/* package */ class SEyeDvsConfig extends DvsConfig {
  public SEyeDvsConfig() {
    // log file parameters
    /** must match name in LogFileLocations and be an extract of a recording with the siliconEye */
    logFileLocations = LogFileLocations.DUBISiliconEyeG;
    width = RealScalar.of(320);
    height = RealScalar.of(264);
    channel_DVS = "seye.overview.aedvs";
    dvsLcmClient = new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    /** maxDuration */
    logFileDuration = Quantity.of(40, SI.SECOND);
    // general parameters
    /** time threshold for background activity filter
     * the report 20180225_davis240c_event_distribution concludes:
     * 1) a 4[s] recording of rapid turning contains 975 intervals
     * of duration at least 1[ms] during which no events occur
     * 2) for a bin of width 500[us] chances are p=0.30283 that the bin is empty
     * 3) for a bin size of 2397[us] there is a 99% chance that it’s non-empty */
    filterConstant = Quantity.of(500, NonSI.MICRO_SECOND);
    /** [-] for FAST corner filter */
    margin = RealScalar.of(4);
  }
}
