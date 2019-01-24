// code by jph
package ch.ethz.idsc.retina.util.tty;

public interface SerialPortInterface extends RingBufferReader {
  /** writes given data via serial port
   * 
   * @param data
   * @return */
  int write(byte[] data);
}
