// code by jph
package ch.ethz.idsc.retina.util.io;

/** consumer of a byte array */
@FunctionalInterface
public interface ByteArrayConsumer {
  /** @param data
   * @param length */
  void accept(byte[] data, int length);

  /** @param data */
  default void accept(byte[] data) {
    accept(data, data.length);
  }
}
