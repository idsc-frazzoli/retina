// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

/** 7 different values:
 * 3 axes for accel, temperature, and 3 axes */
public class ImuDavisEvent {
  public final int time;
  public final int data;
  public final int index;
  public final short value;

  // highest bit of data is aps flag == 1
  public ImuDavisEvent(int time, int data) {
    this.time = time;
    this.data = data;
    index = (data >> 28) & 0x7;
    value = (short) ((data >> 12) & 0xffff);
  }
}
