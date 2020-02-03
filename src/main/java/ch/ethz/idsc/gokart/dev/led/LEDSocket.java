// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.util.Optional;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.tty.SerialPorts;
import com.fazecast.jSerialComm.SerialPort;

public final class LEDSocket implements StartAndStoppable {
  private static final String PORT = "/dev/ttyUSB0";
  private static final int BAUD_RATE = 9600;
  private static final int BYTE_SIZE = 8;
  // ---
  private SerialPort serialPort = null;

  public static final LEDSocket INSTANCE = new LEDSocket();

  @Override // from StartAndStoppable
  public void start() {
    serialPort = SerialPorts.create(PORT, BAUD_RATE, BYTE_SIZE);
    serialPort.openPort();
  }

  @Override // from StartAndStoppable
  public void stop() {
    Optional.ofNullable(serialPort).ifPresent(SerialPort::closePort);
    serialPort = null;
  }

  public boolean isOpen() {
    return Optional.ofNullable(serialPort).map(SerialPort::isOpen).orElse(false);
  }

  public void write(byte[] data) {
    if (!isOpen())
      throw new RuntimeException("Port: " + PORT + " is currently not open!");
    serialPort.writeBytes(data, data.length);
  }
}
