// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.io.Serializable;

import ch.ethz.idsc.retina.davis.DavisEvent;

/** 7 different values: 3 axes for accel, temperature, and 3 axes */
public class DavisImuEvent implements DavisEvent, Serializable {
  public final int time;
  /** index ranges from [0, 1, ..., 6] */
  public final int index;
  public final short value;

  // highest bit of data is aps flag == 1
  public DavisImuEvent(int time, int data) {
    this.time = time;
    index = (data >> 28) & 0x7;
    value = (short) ((data >> 12) & 0xffff); // reference data sheet for formula
  }

  @Override
  public int time() {
    return time;
  }
}
