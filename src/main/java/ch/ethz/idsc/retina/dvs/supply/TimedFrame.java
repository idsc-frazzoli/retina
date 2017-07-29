// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import ch.ethz.idsc.tensor.Tensor;

public class TimedFrame {
  public final long time_us;
  public final Tensor frame;

  public TimedFrame(long time_us, Tensor frame) {
    this.time_us = time_us;
    this.frame = frame;
  }
}
