// code by jph
package ch.ethz.idsc.retina.util.data;

import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;

public final class Word {
  public static Word createShort(String string, short value) {
    return new Word(string, value, 2);
  }

  public static Word createByte(String string, byte value) {
    return new Word(string, value, 1);
  }

  /** @param list of words to extract word of given value
   * @param value
   * @return */
  public static Word findShort(List<Word> list, short value) {
    return list.stream().filter(word -> word.getShort() == value).findFirst().get();
  }

  // ---
  private final String string;
  private final long value;
  private final int bytes;

  private Word(String string, long value, int bytes) {
    this.string = string;
    this.value = value;
    this.bytes = bytes;
  }

  public byte getByte() {
    GlobalAssert.that(bytes == 1);
    return (byte) value;
  }

  public short getShort() {
    GlobalAssert.that(bytes == 2);
    return (short) value;
  }

  @Override
  public String toString() {
    String hex = String.format("%016X", value);
    hex = hex.substring(hex.length() - 2 * bytes);
    return String.format("%s = 0x%s", string, hex);
  }
}
