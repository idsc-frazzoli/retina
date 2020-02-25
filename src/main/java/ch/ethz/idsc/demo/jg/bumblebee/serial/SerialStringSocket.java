// code by gjoel
package ch.ethz.idsc.demo.jg.bumblebee.serial;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public abstract class SerialStringSocket implements StartAndStoppable, SerialPortDataListener {
  private static final String DELIM = " ";

  private SerialPort serialPort;
  private BufferedReader input;
  private BufferedWriter output;
  private Timer timer;

  public SerialStringSocket(String port) {
    this(port, 2000000);
  }

  public SerialStringSocket(String port, int baudRate) {
    try {
      serialPort = SerialPort.getCommPort(port);
      serialPort.setBaudRate(baudRate);
      serialPort.openPort();

      input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(serialPort.getOutputStream()));

      serialPort.addDataListener(this);
    } catch (Exception e) {
      e.printStackTrace();
      serialPort = null;
    }
  }

  @Override // from SerialPortDataListener
  public int getListeningEvents() {
    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
  }

  @Override // from SerialPortDataListener
  public synchronized void serialEvent(SerialPortEvent serialPortEvent) {
    // System.out.println(input.lines().count() + " lines in input buffer");
    try {
      String msg = input.readLine();
      // System.out.println(input.lines().count() + " additional lines in input buffer");
      if (msg.startsWith("error"))
        throw new RuntimeException(msg.substring(5));

      receive(msg.split(DELIM));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override // from StartAndStoppable
  public final void start() {
    Optional.ofNullable(serialPort).ifPresent(port -> port.addDataListener(this));
    // Optional.ofNullable(serialPort).ifPresent(SerialPort::openPort);
    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if(Optional.ofNullable(serialPort).isPresent())
          loop();
        else
          System.err.println("No serial port found!");
      }
    }, 70, getPutPeriod_ms());
  }

  @Override // from StartAndStoppable
  public final void stop() {
    Optional.ofNullable(serialPort).ifPresent(SerialPort::removeDataListener);
    Optional.ofNullable(serialPort).ifPresent(SerialPort::closePort);
    if (Objects.nonNull(timer)) {
      timer.cancel();
      timer = null;
    }
  }

  protected void writeln(String str) throws IOException {
    String msg = str + "\n";
    output.write(msg, 0, msg.length());
    output.flush();
  }

  protected abstract long getPutPeriod_ms();

  protected abstract void loop();

  protected abstract void receive(String... msgs);

  // TODO remove after debugging
  public static void main(String[] args) {
    AtomicInteger ai = new AtomicInteger();
    SerialStringSocket socket = new SerialStringSocket("COM6") {
      @Override
      protected long getPutPeriod_ms() {
        return 1000;
      }

      @Override
      protected void loop() {
        try {
          System.out.println(String.format("Send message %d.", ai.get()));
          writeln(String.valueOf(ai.get() % 2 == 0 ? 255 : -256));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      @Override
      protected void receive(String... words) {
        System.out.println(String.format("Received answer %d:", ai.getAndIncrement()));
        Arrays.stream(words).map(Integer::parseInt).forEach(System.out::println);
      }
    };

    socket.start();
    while (ai.get() < 10) {
      // deliberately empty
    }
    socket.stop();
  }
}
