// code by jph
package ch.ethz.idsc.retina.util;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum HexStrings {
  ;
  /** @param data
   * @param length
   * @return bytes as pairs of */
  public static String from(byte[] data, int length) {
    return IntStream.range(0, length) //
        .mapToObj(i -> String.format("%02x", data[i] & 0xff)) //
        .collect(Collectors.joining(" "));
  }

  public static String from(byte[] data) {
    return from(data, data.length);
  }
}
