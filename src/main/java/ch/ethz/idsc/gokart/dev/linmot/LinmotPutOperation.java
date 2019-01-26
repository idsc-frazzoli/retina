// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;

/** class manages the sequence of position commands
 * every new position value has to insert a changing counter in
 * the motion command header field of the {@link LinmotPutEvent} */
public enum LinmotPutOperation {
  INSTANCE;
  // ---
  private static final Interpolation INTERPOLATION_POSITION = //
      LinearInterpolation.of(Tensors.vector( //
          LinmotPutHelper.TARGETPOS_INIT, //
          LinmotPutHelper.TARGETPOS_MIN));
  // ---
  private int pos_last;
  private int count = 0;

  // ---
  /** off-mode event is used as fallback control and when
   * human driver takes over control of the break by foot */
  public LinmotPutEvent offMode() {
    return configuration(LinmotPutHelper.CMD_OFF_MODE, LinmotPutHelper.MC_ZEROS);
  }

  /** function generates messages for calibration and linmot de-activation.
   * the values that determine position control are all set to zero.
   * 
   * @param control
   * @param motion command header
   * @return */
  /* package */ LinmotPutEvent configuration(Word control, Word motion) {
    return generic(control, motion, (short) 0, (short) 0, (short) 0, (short) 0);
  }

  public LinmotPutEvent generic(Word control, Word motion_cmd_hdr, //
      short target_position, short max_velocity, short acceleration, short deceleration) {
    if (motion_cmd_hdr == LinmotPutHelper.MC_POSITION)
      return toPosition(control, target_position);
    return new LinmotPutEvent(control, motion_cmd_hdr.getShort(), target_position, max_velocity, acceleration, deceleration);
  }

  /** @param value in the unit interval [0, 1]
   * @return
   * @throws Exception if value is outside the unit interval [0, 1] */
  public LinmotPutEvent toRelativePosition(Scalar value) {
    return toPosition( //
        LinmotPutHelper.CMD_OPERATION, //
        INTERPOLATION_POSITION.At(value).number().shortValue());
  }

  /** @param value
   * @return
   * @throws Exception if value is outside */
  public LinmotPutEvent absolutePosition(short value) {
    return toPosition( //
        LinmotPutHelper.CMD_OPERATION, //
        value);
  }

  public LinmotPutEvent turnOff() {
    // just turn it off
    return toPosition(//
        LinmotPutHelper.CMD_OFF_MODE, //
        LinmotPutHelper.TARGETPOS_INIT);
  }

  /** @return command that sets the brake to home position */
  public LinmotPutEvent fallback() {
    return toPosition( //
        LinmotPutHelper.CMD_OPERATION, //
        LinmotPutHelper.TARGETPOS_INIT);
  }

  /** @param pos
   * @return */
  private synchronized LinmotPutEvent toPosition(Word control, short pos) {
    if (pos_last != pos) {
      pos_last = pos;
      ++count;
      count &= 0xf;
    }
    short motion = LinmotPutHelper.MC_POSITION.getShort();
    motion |= count & 0xf;
    return new LinmotPutEvent( //
        control, //
        motion, pos, //
        LinmotPutHelper.MAXVELOCITY_INIT, //
        LinmotPutHelper.ACCELERATION_INIT, //
        LinmotPutHelper.DECELERATION_INIT);
  }
}
