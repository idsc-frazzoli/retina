// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;

/** for SLAM algorithm modules that execute on a periodic basis */
public abstract class PeriodicSlamStep extends AbstractSlamStep {
  protected final int updatePeriod; // [us]
  // ---
  protected Integer lastComputationTimeStamp = null;

  protected PeriodicSlamStep(SlamContainer slamContainer, Scalar updatePeriod) {
    super(slamContainer);
    this.updatePeriod = Magnitude.MICRO_SECOND.toInt(updatePeriod);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    initializeTimeStamps(davisDvsEvent.time);
    if (davisDvsEvent.time - lastComputationTimeStamp > updatePeriod) {
      periodicTask(davisDvsEvent.time, lastComputationTimeStamp);
      lastComputationTimeStamp = davisDvsEvent.time;
    }
  }

  protected void initializeTimeStamps(int initTimeStamp) {
    if (Objects.isNull(lastComputationTimeStamp))
      lastComputationTimeStamp = initTimeStamp;
  }

  /** function invoked on a periodic basis given by {@link updatePeriod}, time stamps are provided
   * by the davis dvs event stream
   * @param currentTimeStamp of current davisDvsEvent
   * @param lastComputationTimeStamp of davisDvsEvent for which function has been executed the last time */
  abstract protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp);
}
