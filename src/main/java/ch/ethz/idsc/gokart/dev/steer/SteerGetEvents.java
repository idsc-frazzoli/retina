// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.nio.ByteBuffer;

public enum SteerGetEvents {
  ;
  public static final SteerGetEvent ZEROS = //
      new SteerGetEvent(ByteBuffer.wrap(new byte[SteerGetEvent.LENGTH]));
}
