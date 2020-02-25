// code by gjoel
package ch.ethz.idsc.demo.jg.bumblebee.steer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.gokart.core.PutListener;
import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.core.RankedPutProviders;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.tty.SerialPorts;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class BBSteerSocket implements StartAndStoppable, SerialPortDataListener {
  private static final String PORT = "COM6"; // "/dev/ttyUSB1";
  private static final int BAUD_RATE = 2000000; // 9600;
  private static final int SEND_PERIOD_MS = 10;

  public static final BBSteerSocket INSTANCE = new BBSteerSocket();

  // ---
  private SerialPort serialPort;
  private InputStream input;
  private OutputStream output;
  private final byte[] buffer = new byte[BBSteerGetEvent.LENGTH];

  private final List<GetListener<BBSteerGetEvent>> getListeners = new CopyOnWriteArrayList<>();
  /* package */ final ByteArrayConsumer byteArrayConsumer = new ByteArrayConsumer() {
    @Override // from ByteArrayConsumer
    public void accept(byte[] data, int length) {
      ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, length);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      BBSteerGetEvent getEvent = new BBSteerGetEvent(byteBuffer);
      synchronized (getListeners) {
        for (GetListener<BBSteerGetEvent> listener : getListeners)
          try {
            listener.getEvent(getEvent); // notify get listener
          } catch (Exception exception) {
            exception.printStackTrace();
          }
      }
    }
  };
  // ---
  private final RankedPutProviders<BBSteerPutEvent> rankedPutProviders = new RankedPutProviders<>();
  private final List<PutListener<BBSteerPutEvent>> putListeners = new CopyOnWriteArrayList<>();
  private Timer timer;

  private BBSteerSocket() {
    try {
      serialPort = SerialPorts.create(PORT, BAUD_RATE, BBSteerGetEvent.LENGTH);
      input = serialPort.getInputStream();
      output = serialPort.getOutputStream();
      serialPort.addDataListener(this);

      addPutProvider(BBSteerPutFallback.INSTANCE);
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
    try {
      final int bufferSize = input.available();
      if (bufferSize > BBSteerGetEvent.LENGTH) {
        input.skip(bufferSize - BBSteerGetEvent.LENGTH);
        GlobalAssert.that(input.read(buffer) == BBSteerGetEvent.LENGTH);
      }
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
        if(Optional.ofNullable(serialPort).isPresent()) {
          byteArrayConsumer.accept(buffer);

          for (List<PutProvider<BBSteerPutEvent>> putProviders : rankedPutProviders.values())
            for (PutProvider<BBSteerPutEvent> putProvider : putProviders) {
              Optional<BBSteerPutEvent> optional = putProvider.putEvent();
              if (optional.isPresent()) {
                BBSteerPutEvent putEvent = optional.get();
                try {
                  // ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[BBSteerPutEvent.LENGTH]);
                  // byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                  // putEvent.insert(byteBuffer);
                  // output.write(byteBuffer.array());
                  output.write(putEvent.asArray());
                  output.flush();

                  for (PutListener<BBSteerPutEvent> putListener : putListeners)
                    putListener.putEvent(putEvent); // notify put listener
                  return;
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
          System.err.println("no command provided in " + getClass().getSimpleName());
        } else
          System.err.println("No serial port found!");
      }
    }, 70, SEND_PERIOD_MS);
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

  /***************************************************/
  public final void addPutProvider(PutProvider<BBSteerPutEvent> putProvider) {
    boolean added = rankedPutProviders.add(putProvider);
    if (!added) {
      System.err.println(putProvider.getClass().getSimpleName());
      new RuntimeException("put provider not added").printStackTrace();
    }
  }

  public final void removePutProvider(PutProvider<BBSteerPutEvent> putProvider) {
    boolean removed = rankedPutProviders.remove(putProvider);
    if (!removed) {
      System.err.println(putProvider.getClass().getSimpleName());
      new RuntimeException("put provider not removed").printStackTrace();
    }
  }

  /***************************************************/
  public final void addGetListener(GetListener<BBSteerGetEvent> getListener) {
    synchronized (getListeners) {
      boolean added = getListeners.add(getListener);
      if (!added) {
        System.err.println(getListener.getClass().getSimpleName());
        new RuntimeException("get listener not added").printStackTrace();
      }
    }
  }

  public final void removeGetListener(GetListener<BBSteerGetEvent> getListener) {
    synchronized (getListeners) {
      boolean removed = getListeners.remove(getListener);
      if (!removed) {
        System.err.println(getListener.getClass().getSimpleName());
        new RuntimeException("get listener not removed").printStackTrace();
      }
    }
  }

  /***************************************************/
  public final void addPutListener(PutListener<BBSteerPutEvent> putListener) {
    boolean added = putListeners.add(putListener);
    if (!added) {
      System.err.println(putListener.getClass().getSimpleName());
      new RuntimeException("put listener not added").printStackTrace();
    }
  }

  public final void removePutListener(PutListener<BBSteerPutEvent> putListener) {
    boolean removed = putListeners.remove(putListener);
    if (!removed) {
      System.err.println(putListener.getClass().getSimpleName());
      new RuntimeException("put listener not removed").printStackTrace();
    }
  }
}
