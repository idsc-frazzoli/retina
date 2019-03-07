// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;
import java.util.Objects;
import java.util.PriorityQueue;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Increment;
import ch.ethz.idsc.tensor.sca.Log;

public class SimpleFrameDvsEventSupplier implements FrameDvsEventSupplier {
  private final Dimension dimension;
  private final PriorityQueue<DvsEvent> queue = new PriorityQueue<>();
  private Tensor sbuf;
  // ---
  private Scalar theta = DoubleScalar.of(0.08);

  public SimpleFrameDvsEventSupplier(Dimension dimension) {
    this.dimension = dimension;
  }

  @Override
  public void handle(TimedFrame timedFrame) {
    Tensor ins = timedFrame.frame.map(scalar -> Log.of(Increment.ONE.apply(scalar)));
    if (Objects.isNull(sbuf))
      sbuf = ins.copy();
    for (int x = 0; x < dimension.width; ++x)
      for (int y = 0; y < dimension.height; ++y) {
        while (Scalars.lessThan(sbuf.Get(x, y).add(theta), ins.Get(x, y))) {
          sbuf.set(s -> s.add(theta), x, y);
          queue.add(new DvsEvent(timedFrame.time_us, x, y, 1));
        }
        while (Scalars.lessThan(ins.Get(x, y), sbuf.Get(x, y).subtract(theta))) {
          sbuf.set(s -> s.subtract(theta), x, y);
          queue.add(new DvsEvent(timedFrame.time_us, x, y, 0));
        }
      }
  }

  @Override
  public boolean isEmpty() {
    return queue.isEmpty();
  }

  @Override
  public DvsEvent next() throws Exception {
    // does not throw an exception even if queue is empty
    // returns null if queue is empty
    return queue.poll();
  }

  @Override
  public Dimension dimension() {
    return dimension;
  }
}
