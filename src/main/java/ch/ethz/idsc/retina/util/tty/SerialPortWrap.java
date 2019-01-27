// code by swisstrolley+
// code by jph
package ch.ethz.idsc.retina.util.tty;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortWrap implements Runnable, SerialPortInterface, AutoCloseable {
  private static final int BUFFER_SIZE = 4096;
  private static final int SLEEP_MS = 1;
  // ---
  private final SerialPort serialPort;
  private final InputStream inputStream;
  /** storage of received bytes */
  private final byte[] rxData = new byte[BUFFER_SIZE];
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(rxData);
  private final Thread thread;
  // ---
  private boolean isLaunched = true;
  private int rxHead = 0;
  private int rxTail = 0;
  private int nBytesInBuffer = 0;

  /** @param serialPort open */
  public SerialPortWrap(SerialPort serialPort) {
    if (!serialPort.isOpen())
      throw new RuntimeException();
    this.serialPort = serialPort;
    inputStream = serialPort.getInputStream();
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    // ---
    thread = new Thread(this);
    thread.start();
  }

  @Override // from Runnable
  public void run() {
    try {
      while (isLaunched) {
        int nRead = inputStream.available();
        if (0 < nRead)
          synchronized (byteBuffer) {
            int length = Math.min(nRead, rxData.length - rxHead); // max number of bytes to read
            int rxRead = inputStream.read(rxData, rxHead, length); // number of bytes effectively read
            rxHead += rxRead;
            rxHead %= BUFFER_SIZE;
            nBytesInBuffer += rxRead;
          }
        else
          try {
            Thread.sleep(SLEEP_MS);
          } catch (Exception exception) {
            // ---
          }
      }
    } catch (Exception exception) {
      // ---
    }
  }

  @Override // from RingBufferExchange
  public boolean peek(byte[] data, int length) {
    synchronized (byteBuffer) {
      if (length <= nBytesInBuffer) {
        for (int i = 0; i < length; ++i)
          data[i] = byteBuffer.get((rxTail + i) % BUFFER_SIZE);
        return true;
      }
    }
    return false;
  }

  @Override // from RingBufferExchange
  public void advance(int length) {
    synchronized (byteBuffer) {
      rxTail += length;
      rxTail %= BUFFER_SIZE;
      nBytesInBuffer -= length;
    }
  }

  @Override // from RingBufferExchange
  public synchronized int write(byte[] data) {
    return serialPort.writeBytes(data, data.length);
  }

  @Override // from AutoCloseable
  public void close() {
    isLaunched = false;
    thread.interrupt();
    serialPort.closePort();
  }
}
