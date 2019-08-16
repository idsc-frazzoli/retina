// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.nio.ByteBuffer;

public enum SteerGetEvents {
  ;
  /** artificial SteerGetEvent message containing zero values */
  public static final SteerGetEvent ZEROS = //
      new SteerGetEvent(ByteBuffer.wrap(new byte[SteerGetEvent.LENGTH]));
}
