// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;

public enum LinmotPutConfiguration {
  ;
  public static final Word CMD_HOME = Word.createShort("HOME", (short) 0x083f);
  public static final Word CMD_OPERATION = Word.createShort("OPERATION", (short) 0x003f);
  public static final Word CMD_ERR_ACK = Word.createShort("ERR_ACK", (short) 0x00bf);
  public static final Word CMD_OFF_MODE = Word.createShort("OFF_MODE", (short) 0x003e);
  public static final Word MC_POSITION = Word.createShort("POSITION", (short) 0x0900);
  public static final Word MC_ZEROS = Word.createShort("ZEROS", (short) 0x0000);
  // ---
  public static final List<Word> COMMANDS = Arrays.asList( //
      CMD_HOME, CMD_OPERATION, CMD_ERR_ACK, CMD_OFF_MODE);
  public static final List<Word> HEADER = Arrays.asList( //
      MC_POSITION, MC_ZEROS); //
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

  public static Word findWord(List<Word> list, short value) {
    return list.stream().filter(w -> w.getShort() == value).findFirst().get();
  }

  public static Word findControlWord(short value) {
    return findWord(COMMANDS, value);
  }

  public static Word findHeaderWord(short value) {
    return findWord(HEADER, value);
  }
}
