// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;

public enum LinmotPutConfiguration {
  ;
  // ---
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createShort("HOME", (short) 0x083f), //
      Word.createShort("OPERATION", (short) 0x003f), //
      Word.createShort("ERR_ACK", (short) 0x00bf), //
      Word.createShort("OFF_MODE", (short) 0x003e) //
  );
  public static final List<Word> HEADER = Arrays.asList( //
      Word.createShort("POSITION", (short) 0x0900), //
      Word.createShort("ZEROS", (short) 0x0000) // <- this message may not be needed
  );
  // ---
  // TODO NRJ document empirical justification for all magic numbers
  public static final int TARGETPOS_MIN = -500;
  public static final int TARGETPOS_MAX = -48;
  public static final int TARGETPOS_INIT = -50;
  // ---
  public static final int MAXVELOCITY_MIN = 0;
  public static final int MAXVELOCITY_MAX = 1000;
  public static final int MAXVELOCITY_INIT = 1000;
  // ---
  public static final int ACCELERATION_MIN = 0;
  public static final int ACCELERATION_MAX = 5000;
  public static final int ACCELERATION_INIT = 500;
  // ---
  public static final int DECELERATION_MIN = 0;
  public static final int DECELERATION_MAX = 5000;
  public static final int DECELERATION_INIT = 500;
}
