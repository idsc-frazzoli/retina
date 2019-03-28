// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.nio.ByteBuffer;

public enum LinmotGetEvents {
  ;
  public static final LinmotGetEvent ZEROS = new LinmotGetEvent(ByteBuffer.wrap(new byte[16]));
}
