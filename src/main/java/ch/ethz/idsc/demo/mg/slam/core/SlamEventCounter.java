package ch.ethz.idsc.demo.mg.slam.core;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.StartAndStoppable;

// counts how many events are processed. By running it both offline and online, we get ratio of 
// processed events
/* package */ class SlamEventCounter extends AbstractSlamStep implements StartAndStoppable {
  private final int updatePeriod = 1000000; // [us]
  // ---
  private Integer lastComputationTimeStamp = null;
  private int secondCount;
  private long totalEventCount;
  private int eventCount;

  SlamEventCounter(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
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
    System.out.println(eventCount + " events processed in last sec");
    totalEventCount += eventCount;
    secondCount++;
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
    System.out.println("Average processed event rate " + totalEventCount / secondCount + " events/sec");
  }
}
