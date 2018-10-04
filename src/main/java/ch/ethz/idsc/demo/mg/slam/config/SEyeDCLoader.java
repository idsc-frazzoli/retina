// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.io.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets DvsConfig parameters according to siliconEye */
/* package */ enum SEyeDCLoader {
  ;
  public static DvsConfig getSlamCoreConfig() {
    DvsConfig dvsConfig = new DvsConfig();
    // log file parameters
    /** must match name in LogFileLocations and be an extract of a recording with the siliconEye */
    dvsConfig.logFileLocations = LogFileLocations.DUBISiliconEyeC;
    dvsConfig.width = RealScalar.of(320);
    dvsConfig.height = RealScalar.of(264);
    dvsConfig.channel_DVS = "seye.overview.aedvs";
    dvsConfig.dvsLcmClient = new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
    dvsConfig.calibration = ResourceData.of("/demo/mg/DUBISiliconEye.csv");
    /** maxDuration */
    dvsConfig.logFileDuration = Quantity.of(50, SI.SECOND);
    // general parameters
    /** time threshold for background activity filter
     * the report 20180225_davis240c_event_distribution concludes:
     * 1) a 4[s] recording of rapid turning contains 975 intervals
     * of duration at least 1[ms] during which no events occur
     * 2) for a bin of width 500[us] chances are p=0.30283 that the bin is empty
     * 3) for a bin size of 2397[us] there is a 99% chance that it’s non-empty */
    dvsConfig.filterConstant = Quantity.of(1500, NonSI.MICRO_SECOND);
    /** [-] for FAST corner filter */
    dvsConfig.margin = RealScalar.of(4);
    return dvsConfig;
  }
}
