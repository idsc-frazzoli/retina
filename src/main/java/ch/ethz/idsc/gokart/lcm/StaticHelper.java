// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.util.function.BinaryOperator;
import java.util.stream.Stream;

enum StaticHelper {
  ;
  private static final BinaryOperator<Integer> PRODUCT = (a, b) -> a * b;

  static int numel(Stream<Integer> stream) {
    return stream.reduce(PRODUCT).orElse(1);
  }
}
