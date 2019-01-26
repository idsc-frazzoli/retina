// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;

public enum LinmotPutHelper {
  ;
  /* package */ static final Word CMD_HOME = Word.createShort("HOME", (short) 0x083f);
  public static final Word CMD_OPERATION = Word.createShort("OPERATION", (short) 0x003f);
  /* package */ static final Word CMD_ERR_ACK = Word.createShort("ERR_ACK", (short) 0x00bf);
  static final Word CMD_OFF_MODE = Word.createShort("OFF_MODE", (short) 0x003e);
  /**
   * 
   */
  public static final Word MC_POSITION = Word.createShort("POSITION", (short) 0x0900);
  /* package */ static final Word MC_ZEROS = Word.createShort("ZEROS", (short) 0x0000);
  // ---
  public static final List<Word> COMMANDS = Arrays.asList( //
      CMD_HOME, CMD_OPERATION, CMD_ERR_ACK, CMD_OFF_MODE);
  public static final List<Word> HEADER = Arrays.asList( //
      MC_POSITION, MC_ZEROS); //
  // ---
  /** all magic numbers are justified through experimentation
   * the number -500 corresponds to an elogation of 50[mm] to the front */
  public static final int TARGETPOS_MIN = -500;
  /** the number -48 corresponds to an elogation of 4.8[mm] to the front */
  public static final int TARGETPOS_MAX = -48;
  public static final short TARGETPOS_INIT = -50;
  // ---
  public static final int MAXVELOCITY_MIN = 0;
  public static final int MAXVELOCITY_MAX = 1000;
  public static final short MAXVELOCITY_INIT = 1000;
  // ---
  public static final int ACCELERATION_MIN = 0;
  public static final int ACCELERATION_MAX = 5000;
  public static final short ACCELERATION_INIT = 500;
  // ---
  public static final int DECELERATION_MIN = 0;
  public static final int DECELERATION_MAX = 5000;
  public static final short DECELERATION_INIT = 500;

  // ---
  public static Word findControlWord(short value) {
    return Word.findShort(COMMANDS, value);
  }

  public static Word findHeaderWord(short value) {
    return Word.findShort(HEADER, value);
  }
}
