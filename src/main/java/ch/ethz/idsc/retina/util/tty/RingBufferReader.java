// code by jph
package ch.ethz.idsc.retina.util.tty;

public interface RingBufferReader {
  /** @param data
   * @param length
   * @return true if buffer contains at least given length number of bytes
   * in which case the given data array will be filled with the content,
   * else false */
  boolean peek(byte[] data, int length);

  /** advances the peek position of the buffer by given length number of bytes
   * function should only be called after peek has returned true for given length
   * otherwise the behavior is undefined
   * 
   * @param length */
  void advance(int length);
}
