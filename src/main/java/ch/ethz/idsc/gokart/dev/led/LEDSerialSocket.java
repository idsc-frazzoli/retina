// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.tty.SerialPorts;
import com.fazecast.jSerialComm.SerialPort;

/* package */ final class LEDSerialSocket implements StartAndStoppable, PutListener<LEDPutEvent> {
  private static final String PORT = "/dev/ttyUSB1";
  private static final int BAUD_RATE = 115200;
  private static final int BYTE_SIZE = 8;
  // ---
  private SerialPort serialPort = null;

  public static final LEDSerialSocket INSTANCE = new LEDSerialSocket();

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

  @Override // from PutListener
  public void putEvent(LEDPutEvent putEvent) {
    if (isOpen())
      write(putEvent.status);
  }

  public boolean isOpen() {
    return Optional.ofNullable(serialPort).map(SerialPort::isOpen).orElse(false);
  }

  private void write(LEDStatus ledStatus) {
    write(Crc8MaximHelper.convert(ledStatus.asArray()));
  }

  private void write(byte[] data) {
    if (!isOpen())
      throw new RuntimeException("Port: " + PORT + " is currently not open!");
    serialPort.writeBytes(data, data.length);
  }
}
