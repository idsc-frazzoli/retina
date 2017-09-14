// code by jph
package ch.ethz.idsc.retina.util;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum HexStrings {
  ;
  public static String from(byte[] data) {
    return IntStream.range(0, data.length) //
        .mapToObj(i -> String.format("%02x", data[i] & 0xff)) //
        .collect(Collectors.joining(" "));
  }
}
