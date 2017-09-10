// code by jph
package ch.ethz.idsc.retina.gui.gokart;

public class Word {
  public static Word createShort(String string, short value) {
    return new Word(string, value, 2);
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

  @Override
  public String toString() {
    String hex = String.format("%016x", value);
    hex = hex.substring(hex.length() - 2 * bytes);
    return String.format("%s = %s", string, hex);
  }
}
