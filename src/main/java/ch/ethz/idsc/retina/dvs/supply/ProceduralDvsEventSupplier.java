// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;
import java.util.PriorityQueue;

import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.retina.dvs.core.DvsEvent;

public class ProceduralDvsEventSupplier implements DvsEventSupplier {
  private final static long SEC_TO_USEC = 1_000_000;
  // ---
  private final Dimension dimension;
  private final long duration;
  private final PriorityQueue<DvsEvent> priorityQueue = new PriorityQueue<>();

  /** @param dimension
   * @param duration_us */
  public ProceduralDvsEventSupplier(Dimension dimension, long duration_us) {
    this.dimension = dimension;
    this.duration = duration_us;
    for (int x = 0; x < dimension.width; ++x)
      for (int y = 0; y < dimension.height; ++y) {
        double dx = x * 9e-3;
        double dy = y * 5e-3;
        priorityQueue.add(new DvsEvent((long) ((dx + dy) * SEC_TO_USEC), x, y, 1));
      }
  }

  @Override
  public DvsEvent next() throws Exception {
    DvsEvent dvsEvent = priorityQueue.poll();
    long time = dvsEvent.time_us;
    if (duration < time)
      throw new RuntimeException();
    double incr = 0.25 + SimplexContinuousNoise.FUNCTION.at(dvsEvent.x * 0.01, dvsEvent.y * 0.01) * 0.003;
    long next = time + Math.round(incr * SEC_TO_USEC);
    if (next <= time) {
      System.out.println(next + " " + time + " " + incr);
      throw new RuntimeException();
    }
    priorityQueue.add(new DvsEvent(next, dvsEvent.x, dvsEvent.y, 1 - dvsEvent.i));
    return dvsEvent;
  }

  @Override
  public Dimension dimension() {
    return dimension;
  }
}
