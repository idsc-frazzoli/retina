// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.LogFileLocations;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets DvsConfig parameters according to davis */
/* package */ class DavisDvsConfig extends DvsConfig {
  public DavisDvsConfig() {
    /** must match name in LogFileLocations and be an extract of a recording with the davis */
    logFileLocations = LogFileLocations.DUBI20a;
    width = RealScalar.of(240);
    height = RealScalar.of(180);
    dvsLcmClient = new DavisLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
    channel_DVS = "davis240c.overview.dvs";
    /** maxDuration */
    logFileDuration = Quantity.of(55, SI.SECOND);
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
