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
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Increment;
import ch.ethz.idsc.tensor.sca.Log;

public class InterpolatedFrameDvsEventSupplier implements FrameDvsEventSupplier {
  private final Dimension dimension;
  private final PriorityQueue<DvsEvent> queue = new PriorityQueue<>();
  private Tensor sbuf;
  private long time_us0;
  private Tensor ins0;
  // ---
  private Scalar theta = DoubleScalar.of(0.1);

  public InterpolatedFrameDvsEventSupplier(Dimension dimension) {
    this.dimension = dimension;
  }

  @Override
  public void handle(TimedFrame timedFrame) {
    Tensor ins = timedFrame.frame.map(scalar -> Log.of(Increment.ONE.apply(scalar)));
    if (Objects.isNull(sbuf)) {
      sbuf = ins.copy();
      ins0 = ins.copy();
      time_us0 = timedFrame.time_us;
    }
    final Interpolation interpolation = LinearInterpolation.of(Tensors.vector(time_us0, timedFrame.time_us));
    Tensor idif = ins.subtract(ins0);
    for (int x = 0; x < dimension.width; ++x)
      for (int y = 0; y < dimension.height; ++y) {
        while (Scalars.lessThan(sbuf.Get(x, y).add(theta), ins.Get(x, y))) {
          Scalar inx = sbuf.Get(x, y).add(theta);
          Scalar rat = inx.subtract(ins0.get(x, y)).divide(idif.Get(x, y));
          Scalar time = interpolation.At(rat);
          queue.add(new DvsEvent(time.number().longValue(), x, y, 1));
          sbuf.set(inx, x, y);
        }
        while (Scalars.lessThan(ins.Get(x, y), sbuf.Get(x, y).subtract(theta))) {
          Scalar inx = sbuf.Get(x, y).subtract(theta);
          Scalar rat = inx.subtract(ins0.get(x, y)).divide(idif.Get(x, y));
          Scalar time = interpolation.At(rat);
          queue.add(new DvsEvent(time.number().longValue(), x, y, 0));
          sbuf.set(inx, x, y);
        }
      }
    ins0 = ins;
    time_us0 = timedFrame.time_us;
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
