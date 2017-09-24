// code by jph
package ch.ethz.idsc.retina.util.io;

/** listener interface that receives an array of bytes */
public interface ByteArrayConsumer {
  void accept(byte[] data, int length);

  default void accept(byte[] data) {
    accept(data, data.length);
  }
}
