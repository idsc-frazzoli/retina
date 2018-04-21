// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

public enum LinmotBitCycler {
  INSTANCE;
  // ---
  public static final short TARGETPOS_INIT = -50;
  public static final int TARGETPOS_MIN = -500;
  private static final Interpolation INTERPOLATION_POSITION = //
      LinearInterpolation.of(Tensors.vector(TARGETPOS_INIT, TARGETPOS_MIN));
  // ---
  private int pos_last;
  private int count = 0;

  /** @param value in the unit interval [0, 1]
   * @return */
  public LinmotPutEvent operationToRelativePosition(Scalar value) {
    return operationToPosition(INTERPOLATION_POSITION.Get(Tensors.of(value)).number().shortValue());
  }

  /** @param pos
   * @return */
  private LinmotPutEvent operationToPosition(short pos) {
    if (pos_last != pos) {
      pos_last = pos;
      ++count;
    }
    short motion = LinmotPutHelper.MC_POSITION.getShort();
    motion |= count & 0xf;
    return new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, //
        motion, pos, //
        (short) 1000, //
        (short) 500, //
        (short) 500);
  }

  public LinmotPutEvent FALLBACK_OPERATION() {
    return operationToPosition(TARGETPOS_INIT);
  }
}
