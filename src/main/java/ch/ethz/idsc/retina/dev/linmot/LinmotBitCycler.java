// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

public class LinmotBitCycler {
  public static final short TARGETPOS_INIT = -50;
  public static final int TARGETPOS_MIN = -500;
  private static final Interpolation INTERPOLATION_POSITION = //
      LinearInterpolation.of(Tensors.vector(TARGETPOS_INIT, TARGETPOS_MIN));
  // ---
  private int pos_last;
  private int count;

  public LinmotBitCycler() {
  }

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
      // (short)(LinmotPutHelper.MC_POSITION.getShort() | (count&0xf))
    }
    return new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, //
        (short)0, // FIXME
        pos, //
        (short) 1000, //
        (short) 500, //
        (short) 500);
  }
}
