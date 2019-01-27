// code by jph
package ch.ethz.idsc.retina.util.tty;

public interface SerialPortInterface extends RingBufferReader {
  /** writes given data via serial port
   * 
   * @param data
   * @return number of bytes successfully written, or -1 if there was an error writing to the port */
  int write(byte[] data);
}
