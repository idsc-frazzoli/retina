package ch.ethz.idsc.demo.mg.slam.log;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.core.AbstractSlamStep;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;

// counts how many events are processed. By running it both offline and online, we get ratio of 
// processed events
public class SlamEventCounter extends AbstractSlamStep implements StartAndStoppable {
  private static long rawEventCount;
  // ---
  private final int updatePeriod = Magnitude.MICRO_SECOND.toInt(SlamCoreConfig.GLOBAL.logCollectionUpdateRate);
  // ---
  private Integer lastComputationTimeStamp = null;
  private long totalProcessedEventCount;
  private int eventCount;

  public SlamEventCounter(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
  }

  public static void increaseRawEventCount() {
    rawEventCount++;
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    initializeTimeStamps(davisDvsEvent.time);
    eventCount++;
    if (davisDvsEvent.time - lastComputationTimeStamp > updatePeriod) {
      periodictask();
      lastComputationTimeStamp = davisDvsEvent.time;
    }
  }

  private void periodictask() {
    totalProcessedEventCount += eventCount;
    eventCount = 0;
  }

  protected void initializeTimeStamps(int initTimeStamp) {
    if (Objects.isNull(lastComputationTimeStamp))
      lastComputationTimeStamp = initTimeStamp;
  }

  @Override // from StartAndStoppable
  public void start() {
    // ---
  }

  @Override // from StartAndStoppable
  public void stop() {
    // ---
  }

  public long getProcessedEvents() {
    return totalProcessedEventCount;
  }

  public long getRawEvents() {
    return rawEventCount;
  }
}
