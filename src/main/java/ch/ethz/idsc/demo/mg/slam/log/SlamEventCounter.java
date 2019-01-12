// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.core.AbstractSlamStep;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

// counts how many events are processed. By running it both offline and online, we get ratio of 
// processed events
public class SlamEventCounter extends AbstractSlamStep {
  private static long rawEventCount;
  // ---
  private long processedEventCount;

  public SlamEventCounter(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
  }

  // static to access from AbstractFilterHandler
  public static void increaseRawEventCount() {
    rawEventCount++;
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    processedEventCount++;
  }

  public long getProcessedEventCount() {
    return processedEventCount;
  }

  public long getRawEventCount() {
    return rawEventCount;
  }
}
