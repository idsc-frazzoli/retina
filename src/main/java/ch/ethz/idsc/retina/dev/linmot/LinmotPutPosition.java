// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

/** class manages the sequence of position commands
 * every new position value has to insert a changing counter in
 * the motion command header field of the {@link LinmotPutEvent} */
public enum LinmotPutPosition {
  INSTANCE;
  // ---
  private static final Interpolation INTERPOLATION_POSITION = //
      LinearInterpolation.of(Tensors.vector( //
          LinmotPutHelper.TARGETPOS_INIT, //
          LinmotPutHelper.TARGETPOS_MIN));
  // ---
  private int pos_last;
  private int count = 0;

  /** @param value in the unit interval [0, 1]
   * @return */
  public LinmotPutEvent toRelativePosition(Scalar value) {
    return toPosition(INTERPOLATION_POSITION.Get(Tensors.of(value)).number().shortValue());
  }

  /** @return command that sets the brake to home position */
  public LinmotPutEvent fallback() {
    return toPosition(LinmotPutHelper.TARGETPOS_INIT);
  }

  /** @param pos
   * @return */
  private LinmotPutEvent toPosition(short pos) {
    if (pos_last != pos) {
      pos_last = pos;
      ++count;
      count &= 0xf;
    }
    short motion = LinmotPutHelper.MC_POSITION.getShort();
    motion |= count & 0xf;
    return new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, //
        motion, pos, //
        LinmotPutHelper.MAXVELOCITY_INIT, //
        LinmotPutHelper.ACCELERATION_INIT, //
        LinmotPutHelper.DECELERATION_INIT);
  }
}
