// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** information sent to micro-autobox to forward to the linear motor that
 * controls the break of the gokart */
public class LinmotPutEvent extends DataEvent {
  /** 12 bytes encoding length */
  private static final int LENGTH = 12;
  // ---
  public final short control_word;
  /** motion_cmd_hdr is private because the bits of the short value encode two different values:
   * <pre>
   * 0xfff0 motion_cmd
   * 0x000f counter
   * </pre> */
  private final short motion_cmd_hdr;
  public final short target_position;
  public final short max_velocity;
  public final short acceleration;
  public final short deceleration;

  /** universal constructor of messages for linmot.
   * not all parameter combinations make sense.
   * the flexibility is required for testing.
   * 
   * @param control
   * @param motion command header
   * @param target_position between {@link LinmotPutHelper#TARGETPOS_MIN} and {@link LinmotPutHelper#TARGETPOS_MAX}
   * @param max_velocity
   * @param acceleration
   * @param deceleration */
  /* package */ LinmotPutEvent(Word control, short motion_cmd_hdr, //
      short target_position, short max_velocity, short acceleration, short deceleration) {
    control_word = control.getShort();
    this.motion_cmd_hdr = motion_cmd_hdr;
    this.target_position = target_position;
    this.max_velocity = max_velocity;
    this.acceleration = acceleration;
    this.deceleration = deceleration;
  }

  public LinmotPutEvent(ByteBuffer byteBuffer) {
    control_word = byteBuffer.getShort();
    motion_cmd_hdr = byteBuffer.getShort();
    target_position = byteBuffer.getShort();
    max_velocity = byteBuffer.getShort();
    acceleration = byteBuffer.getShort();
    deceleration = byteBuffer.getShort();
  }

  /** @param byteBuffer with at least 12 bytes remaining */
  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(control_word);
    byteBuffer.putShort(motion_cmd_hdr);
    byteBuffer.putShort(target_position);
    byteBuffer.putShort(max_velocity);
    byteBuffer.putShort(acceleration);
    byteBuffer.putShort(deceleration);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  public boolean isOperational() {
    return control_word == LinmotPutHelper.CMD_OPERATION.getShort() //
        && getMotionCmdHeaderWithoutCounter() == LinmotPutHelper.MC_POSITION.getShort();
  }

  /** the last lowest 4 bits of motion_cmd_hdr contain a counter
   * 
   * @return motion_cmd_hdr with bits of counter == 0 */
  public short getMotionCmdHeaderWithoutCounter() {
    return (short) (motion_cmd_hdr & 0xfff0);
  }

  public byte getMotionCmdHeaderCounter() {
    return (byte) (motion_cmd_hdr & 0xf);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector( //
        control_word & 0xffff, //
        motion_cmd_hdr & 0xffff, //
        target_position, //
        max_velocity, //
        acceleration, //
        deceleration);
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d", //
        control_word, motion_cmd_hdr, //
        target_position, max_velocity, //
        acceleration, deceleration);
  }
}
